import json
import psycopg2
from psycopg2.extras import RealDictCursor
from opensearchpy import OpenSearch, RequestsHttpConnection, AWSV4SignerAuth
import boto3
import json
import os

client = boto3.client('cognito-idp')
user_pool_id=os.getenv('UserPoolId')

username=os.getenv('DBUsername')
passcode=os.getenv('DBPassword')
hostadress=os.getenv('DBHost')
DBname=os.getenv('DBName')

con = None
region = ''
host = ''
index = ''
service = ''

def get_user_role(event):
    body = event['body']
    if isinstance(body, str):
            body = json.loads(event['body'])
    username= body['username']
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups=response['Groups']
        for group in groups:
            if group['GroupName'] == 'Admin':
                group_name='Admin'
                break;
            elif group['GroupName'] == 'Manager':
                group_name="Manager"
                break;
            elif group['GroupName'] == 'guestUser':
                group_name="guestUser"
                break;
            elif group['GroupName'] == 'normalUser':
                group_name="normalUser"
                break;
        return group_name
    except client.exceptions.UserNotFoundException:
        return None 
    except client.exceptions.ClientError as error:
        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }

def get_db_connection():
    global con
    if con is None or con.closed:
        
        con = psycopg2.connect(
            host=hostadress,
            database=DBname,
            user=username,
            password=passcode
            
         )
        
    return con


def get_opensearch_client():
    auth=(os.getenv('SEUsername'), os.getenv('SEPassword'))
    client = OpenSearch(
        hosts = [{'host': host, 'port': 443}],
        http_auth = auth,
        use_ssl = True,
        verify_certs = True,
        connection_class = RequestsHttpConnection,
        pool_maxsize = 20
    )

    return client

def lambda_handler(event, context):
    if 'body' not in event:
        event = {'body': event}
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)
    
    user_role = get_user_role(event)
    print(user_role)
    if user_role == None:
        return {
            "statusCode": 403,
            "body": "You are not authorized to perform this action"
        }
    if user_role == 'guestUser':
        return {
            "statusCode": 403,
            "body": "You are not authorized to perform this action and need to be registered fully."
        }
    
    if user_role == 'normalUser':
        return {
            "statusCode": 403,
            "body": "You are not authorized to perform this action and need permission."
        }
  
    try:  
        SQLQuery= "delete from items where item_id = %s and organizationid=%s"
        parameters = (event['body']['item_id'],1)
        curr.execute(SQLQuery, parameters)

        OS_client=get_opensearch_client()
        response = OS_client.delete(
            index = 'items',
            id = event['body']['item_id']
        )
        print(response) 
        conn.commit()
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'Error Deleting item',
             'error': json.dumps(str(e))
         }
    finally:
        curr.close()
        conn.close()
       
    return {
            'statusCode': 200,
            'body': 'item deleted successfully'
        }

event={
        'username':'tshegofatso.mapheto7@gmail.com',
        'item_id':36,
        'organizationId':1   
}

print(lambda_handler(event, None))