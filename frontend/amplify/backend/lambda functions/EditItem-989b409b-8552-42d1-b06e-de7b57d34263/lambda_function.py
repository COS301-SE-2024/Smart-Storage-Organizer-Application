import json
import psycopg2
from psycopg2.extras import RealDictCursor 
from opensearchpy import OpenSearch, RequestsHttpConnection, AWSV4SignerAuth
import boto3
import json
import os
import logging


DB_hostadress = os.getenv('DBHost')
DB_name = os.getenv('DBName')
DB_username = os.getenv('DBUsername')
DB_Password = os.getenv('DBPassword')
user_pool_id=os.getenv('UserPoolId')
con = None

region = os.getenv('Region') 
SE_host = os.getenv('SEHost') 
index = os.getenv('Index')
SE_service = os.getenv('Service')
SE_Username = os.getenv('SEUsername')
SE_password = os.getenv('SEPassword')
logging.basicConfig(level=logging.DEBUG)
client = boto3.client('cognito-idp', region_name='us-east-1')


def get_user_role(event):
    body= event['body']
    if isinstance(body, str):
            body = json.loads(body)
    username= body['username']
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups=response['Groups']
        logging.debug(f'Groups: {groups}')
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
        logging.error('User not found')
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
            host=DB_hostadress,
            database=DB_name,
            user=DB_username,
            password=DB_Password            
         )
        
    return con


def get_opensearch_client():
    # auth=(os.getenv('SEUsername'), os.getenv('SEPassword'))
    auth=(SE_Username, SE_password)
    client = OpenSearch(
        hosts = [{'host': SE_host, 'port': 443}],
        http_auth = auth,
        use_ssl = True,
        verify_certs = True,
        connection_class = RequestsHttpConnection,
        pool_maxsize = 20
    )
  
    return client

def check_item_exists(conn, item_id):
    try:
        with conn.cursor(cursor_factory=RealDictCursor) as cursor:
            cursor.execute("SELECT 1 FROM items WHERE item_id = %s", (item_id,))
            return cursor.fetchone() is not None
    except Exception as e:
        print(f"Error checking item existence: {str(e)}")
        return False



def lambda_handler(event, context):
    if 'body' not in event:
        event = {'body': event}
    conn = None
    curr = None
    
    try:
            
        if event.get('body') is None:
            raise ValueError("Event body is None")

        body = event['body']
        if isinstance(body, str):
            body = json.loads(body)
        
        if 'item_id' not in body:
            raise ValueError("item_id not found in body")
        
        item_id = body['item_id']
        
        
        user_role = get_user_role(event)
        if user_role == None:
            return {
            "statusCode": 403,
            "body": "User role not found or unauthorized"
        }
        if user_role == 'guestUser':
            return {
            "statusCode": 403,
            "body": "User role not found or unauthorized"
        }
           
        conn = get_db_connection()
        curr = conn.cursor(cursor_factory = RealDictCursor)

          
        SQLQuery= "UPDATE Items SET item_name = %s, description = %s, colourcoding=%s, barcode=%s, qrcode=%s, quanity=%s, location=%s, item_image=%s ,parentcategoryid=%s, subcategoryid=%s WHERE item_id=%s and organizationid=%s returning *;"
        parameters = (body['item_name'], body['description'], body['colourcoding'], body['barcode'], body['qrcode'], body['quanity'], body['location'], body['item_image'], body['parentcategoryid'], body['subcategoryid'] , body['item_id'], body['organizationid'])
        curr.execute(SQLQuery, parameters)
        item=curr.fetchall()
        
        if item is None or len(item) == 0:
            return {
                'statusCode': 500,
                'body': 'Error modifying item',
                'error': 'Item not found'
            }
        
        item[0]['created_at'] = item[0]['created_at'].strftime('%Y-%m-%d %H:%M:%S')
        item=item[0]
        
        OS_client=get_opensearch_client()
        UpdatedDoc = {
        'doc': {
            "item_name": item['item_name'],
            "description": item['description'],
            "colourcoding": item['colourcoding'],
            "barcode": item['barcode'],
            "qrcode": item['qrcode'],
            "quanity": item['quanity'],
            "location": item['location'],
            "item_image": item['item_image'],
            "parentcategoryid": item['parentcategoryid'],
            "subcategoryid": item['subcategoryid'],
            "created_at": item['created_at']
            }
        }       
       
        response = OS_client.update(
            index="items",
            body=UpdatedDoc,
            id=item_id
        )
        conn.commit()
        
        print('response',response)
        
        return{
            'statusCode': 200,
            'body': 'item modified successfully'
        }
        

    except Exception as e:
        if conn:
            conn.rollback()
        
        return{
                'statusCode': 500,
                'body': 'Error modifying item',
                'error': json.dumps(str(e))
            }
    finally:
        if curr:
            curr.close()
        if conn:
            conn.close()
        