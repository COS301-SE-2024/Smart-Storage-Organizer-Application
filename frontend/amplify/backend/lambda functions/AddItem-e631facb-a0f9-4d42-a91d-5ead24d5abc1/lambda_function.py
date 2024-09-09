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
    # TODO implement
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
          
        SQLQuery= 'INSERT INTO Items (item_name, description, colourcoding, barcode, qrcode, quanity, location, email, item_image, parentcategoryid, subcategoryid, organizationid) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s ) returning *;'
        parameters = (event['item_name'], event['description'], event['colourcoding'], event['barcode'], event['qrcode'], event['quanity'], event['location'], event['email'], event['item_image'], event['category'], event['sub_category'], event['organizationid'])
        curr.execute(SQLQuery, parameters)
        item=curr.fetchall()

        lambda_client = boto3.client('lambda')
        response =  lambda_client.invoke(
            FunctionName='GenerateBarcode',
            InvocationType='RequestResponse', 
            Payload=json.dumps({'itemid': item[0]['item_id']})  
        )        

        payload=response['Payload'].read()
    
        if json.loads(payload)['statusCode'] != 200:
            conn.rollback()
            return{
                "status": json.loads(payload)['statusCode'],
                "message": "Could not generate Barcode. Most likely it timed out",
                "tk":json.loads(payload)
            }
        
        BarcodeUrl=json.loads(payload)['barcodeurl']
    
        lambda_client = boto3.client('lambda')
        response =  lambda_client.invoke(
            FunctionName='GenerateQrcode',
            InvocationType='RequestResponse', 
            Payload=json.dumps({'data': item[0]['item_id']})  
        )

        payload=response['Payload'].read()
    
        if json.loads(payload)['statusCode'] != 200:
            conn.rollback()
            return{
                "status": json.loads(payload)['statusCode'],
                "message": "Could not generate Qrcode. Most likely it timed out"
            }
        QrcodeUrl=json.loads(payload)['qrcodeurl']

        
        SQLQuery= 'Update Items set qrcode=%s, barcode=%s where item_id=%s returning *;'
        parameters = (QrcodeUrl, BarcodeUrl ,item[0]['item_id'] )
        curr.execute(SQLQuery, parameters)

        item=curr.fetchall()
        

        OS_client=get_opensearch_client()
        

        response = OS_client.index(
            index = 'items',
            body = item[0],
            id = item[0]['item_id'],
            refresh = True
        )

        conn.commit()
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'Error inserting data',
             'error': json.dumps(str(e))
         }
    finally:
        curr.close()
        conn.close()
       
    return {
            'statusCode': 200,
            'body': 'Data inserted successfully'
        }
       

