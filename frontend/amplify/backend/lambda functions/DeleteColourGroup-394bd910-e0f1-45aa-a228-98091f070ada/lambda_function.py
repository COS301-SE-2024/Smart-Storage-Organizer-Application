import boto3
import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os
import logging

logging.basicConfig(level=logging.INFO)


client = boto3.client('cognito-idp')
user_pool_id = 'us-east-1_EPbgIUMEQ'

username='MasterUser'
passcode='MasterDb#ss1'
hostadress='Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com'
DBname='postgres'

con = None

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
            host=hostadress,
            database=DBname,
            user=username,
            password=passcode
            
         )
        
    return con

def lambda_handler(event, context):
    conn = None
    curr = None
    try:
        body = event['body']
        if isinstance(body, str):
            body = json.loads(body)
        
        user_role = get_user_role(event)
        print('user_role',user_role)
        if user_pool_id == None:
            return {
                "statusCode": 403,
                "body": "User role not found or unauthorized"
            }

        if user_pool_id == 'guestUser':
            return {
                "statusCode": 403,
                "body": "You are not authorized to perform this action"
            }
            
            
        conn = get_db_connection()
        curr = conn.cursor(cursor_factory = RealDictCursor)
                 
        SQLQuery='delete from colours where id=%s and organizationid=%s'
        parameters = (body['id'], body['organizationid'] )
        curr.execute(SQLQuery, parameters)
        SQLQuery='delete from colourgrouping where colourid=%s and organizationid=%s'
        parameters = (body['id'], body['organizationid'])
        curr.execute(SQLQuery, parameters)
        conn.commit()
        
    
    except Exception as e:
        if conn:
            conn.rollback()
        return {
             'statusCode': 500,
             'body': 'unsuccessful attempt to deleting colour group',
             'error': json.dumps(str(e))
         }
    finally:
        if curr:
            curr.close()
        if conn:
            conn.close()
       
    return {
             'statusCode': 200,
             'status': 'successfully deleted colour group'
        
    }

  

