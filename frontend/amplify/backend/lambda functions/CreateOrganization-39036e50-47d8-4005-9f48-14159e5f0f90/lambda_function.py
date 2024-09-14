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
        
    if user_role == 'normalUser':
        return {
            "statusCode": 403,
            "body": "You are not authorized to perform this action and need to be registered fully."
        }
        
    # if user_role == 'Manager':
    #     return {
    #         "statusCode": 403,
    #         "body": "You are not authorized to perform this action and need to be registered fully."
    #     }

    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        body = json.loads(event['body'])         
        SQLQuery='''
        INSERT INTO organization (name, owner) VALUES (%s, %s) returning *;
        '''
       
        parameters = (body['name'], body['owner'])
        curr.execute(SQLQuery, parameters)
        items=curr.fetchall()
        for item in items:
            serialized_date = item['created_at'].strftime('%Y-%m-%d %H:%M:%S')
            item['created_at']=  serialized_date

        conn.commit()
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'unsuccessful attempt to create organization',
             'error': json.dumps(str(e))
         }
    finally:
        curr.close()
        conn.close()
       
    return {
             'statusCode': 200,
             'status': 'successfully created organization',
             'organization': json.dumps(items[0])
        }


event={
    'body':json.dumps({
        'username':'DaCoda@gmail.com',
        'name':'Skolo',
        'owner':'Thapelo',
        'created_at':'2021-08-23 00:00:00'
    })
    
}

print(lambda_handler(event, ""))