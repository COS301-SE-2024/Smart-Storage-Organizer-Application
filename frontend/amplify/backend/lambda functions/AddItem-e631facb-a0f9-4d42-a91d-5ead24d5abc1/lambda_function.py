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
    # TODO implement
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        body = json.loads(event['body'])  
        SQLQuery= 'INSERT INTO Items (item_name, description, colourcoding, barcode, qrcode, quanity, location, email, item_image, parentcategoryid, subcategoryid, organizationid) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s ) returning *;'
        parameters = (body['item_name'], body['description'], body['colourcoding'], body['barcode'], body['qrcode'], body['quanity'], body['location'], body['email'], body['item_image'], body['category'], body['sub_category'], body['organizationid'])
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
       
event={
    'body':json.dumps({
        'username':'musician.pianist23@gmail.com',
        'item_name':'TestDilo',
        'description':'Television',
        'colourcoding':'default',
        'barcode':'https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/Barcode/barcodes/02510d94-e298-4eda-a65e-d7f9637a8cf4.svg',
        'qrcode':'https://frontend-storage-5dbd9817acab2-dev.s3.amazonaws.com/public/Qrcode/qrcodes/b48736f7-73ea-423a-b9c7-e5d6ae2f3e9b.png',
        'quanity':1,
        'location':'UnitRed',
        'email':'musician.pianist23@gmail.com',
        'item_image':'',
        'category':-1,
        'sub_category':-1,
        'organizationid':1
    })
    
}

print(lambda_handler(event, ""))
