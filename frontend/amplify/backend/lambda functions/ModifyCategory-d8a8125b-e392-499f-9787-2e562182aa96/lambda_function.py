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
    conn = None
    curr = None
    try:
        body = event['body']
        if isinstance(body, str):
            body = json.loads(body)
            
        user_role = get_user_role(event)
        if user_role == None:
            return {
                "statusCode": 403,
                "body": "You are not authorized to perform this action"
            }
            
        if user_role == 'guestUser':
            return {
                "statusCode": 403,
                "body": "You are not authorized to perform this action"
            }
            
        conn = get_db_connection()
        curr = conn.cursor(cursor_factory = RealDictCursor)
        
        SQLQuery= 'UPDATE category SET categoryname = %s where id = %s and organizationid=%s;'
        parameters = (body['categoryname'], body['id'], body['organizationid'])
        curr.execute(SQLQuery, parameters)
        conn.commit()
        
        
    except Exception as e:
        if conn:
            conn.rollback()
        return {
             'statusCode': 500,
             'body': 'Error modifying category name',
             'error': json.dumps(str(e))
        }
    finally:
        if curr is not None:
            curr.close()
        if conn is not None:
            conn.close()
    
    return{
        'statusCode': 200,
        'body': 'category changed successfully'
    }
       

     

    

