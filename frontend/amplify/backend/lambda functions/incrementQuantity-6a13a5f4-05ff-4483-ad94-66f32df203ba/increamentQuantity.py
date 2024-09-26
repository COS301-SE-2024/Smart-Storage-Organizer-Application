import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os
import boto3

client = boto3.client('cognito-idp')
user_pool_id=os.getenv('UserPoolId')

username=os.getenv('DBUsername')
passcode=os.getenv('DBPassword')
hostadress=os.getenv('DBHost')
DBname=os.getenv('DBName')

con = None

def get_user_role(event):
    body = event['body']
    if isinstance(body, str):
        body = json.loads(event['body'])
    user= body['username']
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=user
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
def increment_quantity(conn,curr,event):
    body = event['body']
    if isinstance(body, str):
        body = json.loads(event['body'])
    query = "UPDATE ITEMS SET quanity = %s WHERE item_id = %s"
    parameters = ( body['quantity'], body['item_id'],)
    curr.execute(query,parameters)
    conn.commit()
    return {
        'statusCode': 200,
        'body': json.dumps('Item updated successfully')
    }
    
def lambda_handler(event, context):
    if 'body' not in event:
        event = {'body': event}
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
        
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        response=increment_quantity(conn,curr,event)
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
    return response


