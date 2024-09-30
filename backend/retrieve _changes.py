import json
import os
import sys
import psycopg2
from datetime import datetime
from psycopg2.extras import RealDictCursor
import boto3


def check_access(username):
    try:
        client = boto3.client('cognito-idp', region_name='us-east-1')
        response=client.admin_list_groups_for_user(
                UserPoolId=os.getenv('USER_POOL_ID'),
                Username=username
            )
    except client.exceptions.UserNotFoundException as error:
        return {
            "statusCode": 404,
            "body": 'User not found'
        }
    except client.exceptions.ClientError as error:
        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }
    
    group=response['Groups']
    for item in group:
        if item['GroupName']=='Admin':
            value= 'admin'
            break
        elif item['GroupName']=='normalUser':
            value= 'user'
            break
        elif item['GroupName']=='Manager':
            value ='Manager'
            break
        elif item['GroupName']=='guestUser':
            value= 'guestUser'
            break

    return {
        "statusCode": 200,
        "body":{
            "role": value
        }
    }

con = None
def get_db_connection():
    global con
    if con is None or con.closed:
        con = psycopg2.connect(
            host=os.environ.get('Host_address'),
            database=os.environ.get('DB_Name'),
            user=os.environ.get('Username'),
            password=os.environ.get('Password')

         )
    return con

def lambda_handler(event, context):
    body=event['body']
    if isinstance(body, str):
        body = json.loads(body)

    role = check_access(body['username'])
    if role['statusCode'] != 200:
        return role

    user_role = role['body']['role']
    print(user_role)
    if user_role != 'Admin' and user_role != 'Manager':
        return {
            'statusCode': 401,
            'body': 'Unauthorized'
        }
    try:
        con = get_db_connection()
        cur = con.cursor(cursor_factory=RealDictCursor)
        query="SELECT * FROM changes_made where organization_id=%s"

        parameters = (body['organization_id'],)
        cur.execute(query, parameters)
        rows = cur.fetchall()
        for row in rows:
            for key, value in row.items():
                if isinstance(value, datetime):
                    row[key] = value.isoformat()
    except Exception as e:
        return {
            'statusCode': 500,
            'body': 'Error getting changes',
            'error': str(e)
        }
    return {
        "statusCode": 200,
        "body": json.dumps(rows)
    }

