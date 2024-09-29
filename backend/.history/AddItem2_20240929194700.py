import json
import psycopg2
from psycopg2.extras import RealDictCursor
import boto3

import os

client = boto3.client('cognito-idp', region_name='us-east-1')
user_pool_id = os.getenv('UserPoolId')

host=os.environ.get('Host_address'),
database=os.environ.get('DB_Name'),
user=os.environ.get('Username'),
password=os.environ.get('Password')


con = None


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
        conn = get_db_connection()
        curr = conn.cursor(cursor_factory = RealDictCursor)

        
        body = event['body']
        if isinstance(body, str):
            body = json.loads(event['body'])
        SQLQuery= 'INSERT INTO Items (item_name, description, colourcoding, barcode, qrcode, quanity, location, email, item_image, parentcategoryid, subcategoryid, organizationid) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,%s ) returning item_id;'
        parameters = (body['item_name'], body['description'], body['colourcoding'], body['barcode'], body['qrcode'], body['quanity'], body['location'], body['email'], body['item_image'], body['category'], body['sub_category'], body['organizationid'])
        curr.execute(SQLQuery, parameters)
        inserted_item_id = curr.fetchone()['item_id']
        conn.commit()
        
        return {
            'statusCode': 200,
            'body': json.dumps({'inserted_item_id': inserted_item_id})
        }
    
    except Exception as e:
        conn.rollback()
        return {
            'statusCode': 500,
            'body': json.dumps(f'Error inserting item: {str(e)}')
        }
    finally:
        curr.close()
        conn.close()



