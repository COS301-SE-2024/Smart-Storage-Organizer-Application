import json
import psycopg2
from psycopg2.extras import RealDictCursor

from opensearchpy import OpenSearch, RequestsHttpConnection, AWSV4SignerAuth
import boto3
import json
import os

client = boto3.client('cognito-idp')
user_pool_id = 'us-east-1_EPbgIUMEQ'

username='MasterUser'
passcode='MasterDb#ss1'
hostadress='Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com'
DBname='postgres'

con = None
region = os.getenv('Region') 
host = os.getenv('SEHost') 
index = os.getenv('Index')
service = os.getenv('Service')

def get_user_role(event):
    body = json.loads(event['body'])
    username = body['username']
    try:
        response = client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups = response['Groups'][0]
        group_name = groups['GroupName']
        return {
            "statusCode": 200,
            "body": json.dumps({
                "role": group_name
            })
        }
    except client.exceptions.ClientError as error:
        return None

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
    user_role = get_user_role(event)
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
            "body": "You are not authorized to perform this action and need to be registered fully."
        }
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)
  
    try:
        body = json.loads(event['body'])    
        SQLQuery= "delete from items where item_id = %s and organizationid=%s"
        parameters = (body['item_id'],1)
        curr.execute(SQLQuery, parameters)
        

        OS_client=get_opensearch_client()
        response = OS_client.delete(
            index = 'items',
            id = body['item_id']
        )
        
        print(f"OpenSearch delete response: {response}")

        conn.commit()
        
        return {
            'statusCode': 200,
            'body': 'item deleted successfully'
        }

        
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'Error Deleting item',
             'error': json.dumps(str(e))
         }
    finally:
        try:
            curr.close()
            conn.close()
        except:
            pass
    
    

event={
    'body':json.dumps({
        'username':'musician.pianist23@gmail.com',
        'itemId':25
    })
    
}

print(lambda_handler(event, None))