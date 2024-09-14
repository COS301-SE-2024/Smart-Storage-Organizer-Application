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

def changeCategory(parent_id,sub_parent_category_id,item_id,conn,curr):
        query="update items set parentcategoryid=%s ,subcategoryid=%s where item_id=%s"
        parameters=(parent_id,sub_parent_category_id,item_id)
        curr.execute(query,parameters)
        conn.commit()


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
    
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)
    try:
        body = json.loads(event['body'])
        p_id=body['parentCategoryId']
        s_id=body['subCategoryId']
        i_d=body['itemId']
        changeCategory(p_id,s_id,i_d,conn,curr)
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'Error modifying item',
             'error': json.dumps(str(e))
         }
    
    finally:
        if curr:
            curr.close()
        if conn:
            conn.close()
    return {
        'statusCode': 200,
        'body': 'Item modified successfully'
    }

event={
    'body':json.dumps({
        'username':'DaCoda@gmail.com',
        'parentCategoryId':0,
        'subCategoryId':16,
        'itemId':25
    })
    
}

print(lambda_handler(event, None))
