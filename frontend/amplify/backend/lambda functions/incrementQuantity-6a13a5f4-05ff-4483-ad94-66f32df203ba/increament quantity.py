import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os
import boto3

client = boto3.client('cognito-idp')
user_pool_id = 'us-east-1_EPbgIUMEQ'

username='MasterUser'
passcode='MasterDb#ss1'
hostadress='Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com'
DBname='postgres'

con = None

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
def increment_quantity(conn,curr,event):
    body = json.loads(event['body'])
    query = "UPDATE ITEMS SET quanity = %s item_id = %s"
    parameters = (body['item_id'], body['quantity'],)
    curr.execute(query,parameters)
    conn.commit()
    return {
        'statusCode': 200,
        'body': json.dumps('Item updated successfully')
    }
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
        
    response = {}
    conn = None
    curr = None
    try:
        conn = get_db_connection()
        curr = conn.cursor(cursor_factory = RealDictCursor)
        
        body = json.loads(event['body'])
        query = "UPDATE ITEMS SET quanity = %s item_id = %s"
        parameters = (body['quantity'], body['item_id'],)
        curr.execute(query,parameters)
        conn.commit()
        return {
            'statusCode': 200,
            'body': json.dumps('Item updated successfully')
        }
    
        
    except Exception as e:
      response = {
             'statusCode': 500,
             'body': 'Error modifying item',
             'error': json.dumps(str(e))
         }
    finally:
        if curr is not None:
            curr.close()
        if conn is not None:
            conn.close()
    return response

event={
    'body':json.dumps({
        'username':'musician.pianist23@gmail.com',
        'item_id':25,
        'quantity':3
    })
    
}

print(lambda_handler(event, ""))