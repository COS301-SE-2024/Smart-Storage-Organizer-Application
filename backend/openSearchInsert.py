import json
import psycopg2
from psycopg2.extras import RealDictCursor
from opensearchpy import OpenSearch, RequestsHttpConnection, AWSV4SignerAuth
import boto3
import json
import os

client = boto3.client('cognito-idp', region_name='us-east-1')
user_pool_id = os.getenv('USER_POOL_ID')

username=os.environ.get('Username')
passcode=os.environ.get('Password')
hostadress=os.getenv('Host_address')
DBname=os.getenv('DBName')



region = os.getenv('Region') 
host = os.getenv('SEHost') 
index = os.getenv('Index')
service = os.getenv('Service')
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

def get_user_role(event):
    body = event.get('body')

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

def get_opensearch_client():
    auth=(os.getenv('Username'), os.getenv('Password'))
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
    
    try:
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
        # TODO implement

        conn = get_db_connection()
        curr = conn.cursor(cursor_factory = RealDictCursor)


        body = event['body']
        if isinstance(body, str):
            body = json.loads(event['body'])
    except Exception as e:
        print(e)
        return {
            "statusCode": 500,
            "body": "Internal Server Error"
        }    
    
    try:
        print(body)
        query="""
        SELECT * FROM items where item_id=%s and organizationid=%s"""
        parameter=(body['item_id'],body['organization_id'])
        curr.execute(query,parameter)
        result=curr.fetchone()
        conn.commit()
        if result is  None:
            return {
                "statusCode": 404,
                "body": "Item not found"
            }
        

    except Exception as e:
        print(e)
        return {
            "statusCode": 500,
            "body": "Internal Server Error"
        }
    try:
            print(result)
            body1 = dict(result)
            body1['quanity']=3,
            body1['created_at'] = body1['created_at'].strftime('%Y-%m-%dT%H:%M:%S.%f')
            body1['expiry_date'] = body1['expiry_date'].strftime('%Y-%m-%dT%H:%M:%S')
            
            print(body1)


            print(body1['item_id'])
            OS_client=get_opensearch_client()
            response = OS_client.index(
            index = 'items',
            body = dict(body1),
            id = body1['item_id'],
            refresh = True
            )

            return {
            "statusCode": 200,
            "body": "Item indexed successfully",
            "response": response
            }   
        
    except Exception as e:
        print(e)
        return {
            "statusCode": 500,
            "body": "Internal Server Error ",
            "error": json.dumps(str(e))
        }
    finally:
        conn.close()



