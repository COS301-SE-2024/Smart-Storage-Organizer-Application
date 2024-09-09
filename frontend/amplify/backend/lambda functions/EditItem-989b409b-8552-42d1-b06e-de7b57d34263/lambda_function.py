import json
import psycopg2
from psycopg2.extras import RealDictCursor

from opensearchpy import OpenSearch, RequestsHttpConnection, AWSV4SignerAuth
import boto3
import json
import os

username=os.getenv('DBUsername')
passcode=os.getenv('DBPassword')
hostadress=os.getenv('DBHost')
DBname=os.getenv('DBName')
con = None
region = os.getenv('Region') 
host = os.getenv('SEHost') 
index = os.getenv('Index')
service = os.getenv('Service')

def get_user_role(event):
    body = event['body']
    username = body['username']
    try:
        response = client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups = response['Groups'][0]
        group_name = groups['GroupName']
        return group_name
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
    if user_role is None:
        return {
            "statusCode": 403,
            "body": "User role not found or unauthorized"
        }

    if user_role is 'guestUser':
        return {
            "statusCode": 403,
            "body": "You are not authorized to perform this action"
        }
   
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:    
        SQLQuery= "UPDATE Items SET item_name = %s, description = %s, colourcoding=%s, barcode=%s, qrcode=%s, quanity=%s, location=%s, item_image=%s ,parentcategoryid=%s, subcategoryid=%s WHERE item_id=%s and organizationid=%s returning *;"
        parameters = (event['item_name'], event['description'], event['colourcoding'], event['barcode'], event['qrcode'], event['quanity'], event['location'],event['item_image'],event['parentcategoryid'], event['subcategoryid'] ,event['item_id'], event['organizationid'])
        curr.execute(SQLQuery, parameters)
        item=curr.fetchall()
        item[0]['created_at'] = item[0]['created_at'].strftime('%Y-%m-%d %H:%M:%S')
       
        OS_client=get_opensearch_client()
        UpdatedDoc = {
           'doc': {
               "item_name":item[0]['item_name'],
               "description": item[0]['description'],
               "colourcoding": item[0]['colourcoding'],
               "barcode": item[0]['barcode'],
               "qrcode": item[0]['qrcode'],
               "quanity": item[0]['quanity'],
               "location": item[0]['location'],
               "item_image":item[0]['item_image'],
               "parentcategoryid":item[0]['parentcategoryid'],
               "subcategoryid":item[0]['subcategoryid']
           } 
        }

        response = OS_client.update(
            index="items",
            body=UpdatedDoc,
            id=event['item_id']
        )
        conn.commit()
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'Error modifying item',
             'error': json.dumps(str(e))
         }
    finally:
        curr.close()
        conn.close()
       
    return {
            'statusCode': 200,
            'body': 'item modified successfully'
        }
       
