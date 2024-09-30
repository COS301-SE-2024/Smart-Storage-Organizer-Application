import json
import requests
import boto3
import psycopg2
from psycopg2.extras import RealDictCursor
import json
import os

con = None
host=os.environ.get('Host_address'),
database=os.environ.get('DB_Name'),
user=os.environ.get('Username'),
password=os.environ.get('Password')

client = boto3.client('cognito-idp', region_name='us-east-1')
def get_user_role(event):
    body = event.get('body')

    if isinstance(body, str):
            body = json.loads(event['body'])
    username= body['username']
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=os.getenv('USER_POOL_ID'),
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
        
        print(user_role)
    except Exception as e:

        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }
    
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

        
    body = event['body']
    if isinstance(body, str):
            body = json.loads(event['body'])
    try:
        query="""
        SELECT barcode , qrcode FROM ITEMS WHERE item_id=%s and organizationid=%s """
        parameter=(body['item_id'],body['organization_id'])
        curr.execute(query,parameter)
        result=curr.fetchone()
        conn.commit()
        if result is  None:
            return {
                "statusCode": 404,
                "body": "Item not found"
            }
        return {
            "statusCode": 200,
            "body": json.dumps(result)
        }
    except Exception as e:
        print(e)
        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }



