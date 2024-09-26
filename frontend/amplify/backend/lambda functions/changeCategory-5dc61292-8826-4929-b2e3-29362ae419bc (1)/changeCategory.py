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

def check_access(username):
    try:
        client = boto3.client('cognito-idp',region_name='us-east-1')
        response=client.admin_list_groups_for_user(
                UserPoolId='us-east-1_EPbgIUMEQ',
                Username=username
            )
    except client.exceptions.UserNotFoundException as error:
        print(error)
        return {
            "statusCode": 404,
            "body": 'User not found'
        }
    except client.exceptions.ClientError as error:
        print(error)
        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }
    
    group=response['Groups']
    for item in group:
        if item['GroupName']=='Admin':
            value= 'Admin'
            break
        elif item['GroupName']=='normalUser':
            value= 'normalUser'
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

def changeCategory(parent_id,sub_parent_category_id,item_id,organization_id,conn,curr):
        query="update items set parentcategoryid=%s ,subcategoryid=%s where item_id=%s and organizationid=%s"
        parameters=(parent_id,sub_parent_category_id,item_id,organization_id)
        curr.execute(query,parameters)
        conn.commit()


def lambda_handler(event, context):
    if 'body' not in event:
        event = {'body': event}
    try:
        if 'body' not in event or event['body'] is None:
                return {
                    'statusCode': 400,
                    'body': json.dumps('Bad Request')
                }
    except Exception as e:
        
        return {
            'statusCode': 500,
            'body': 'Error adding item',
            'error': json.dumps(str(e))
        }
    body=event['body']
    if isinstance(body, str):
        body=json.loads(body)
    role=check_access(body['username'])
    if role['statusCode']!=200:
        return role
    user_role=role['body']['role']
    if user_role not in ['Admin','Manager']:
        return {
            'statusCode': 403,
            'body': json.dumps('Forbidden')
        }
    
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)
    try:
        
        body = event['body']
        if isinstance(body, str):
            body = json.loads(event['body'])
        p_id=body['parentCategoryId']
        s_id=body['subCategoryId']
        i_d=body['itemId']
        o_g=body['organization_id']
        changeCategory(p_id,s_id,i_d,o_g,conn,curr)
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
        'username':'Tshegofatso.Mapheto7@gmail.com',
        'parentCategoryId':0,
        'subCategoryId':16,
        'itemId':25,
        'organization_id':1

}

print(lambda_handler(event, None))
