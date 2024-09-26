import boto3
import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os

client = boto3.client('cognito-idp')
user_pool_id=os.getenv('UserPoolId')

username=os.getenv('DBUsername')
passcode=os.getenv('DBPassword')
hostadress=os.getenv('DBHost')
DBname=os.getenv('DBName')

con = None

def get_user_role(event):
    body = event.get('body')
    
    if isinstance(body, str):
            body = json.loads(event['body'])
    username= event['body']['username']
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

    try:         
        Query='''
        INSERT INTO colours (colourcode, organizationid, description, title,createremail) VALUES (%s, %s, %s, %s, %s) RETURNING *;
        '''
        parameters = (event['body']['colourcode'], event['body']['organizationid'], event['body']['description'],event['body']['title'],event['body']['createremail'])
        curr.execute(Query, parameters)
        conn.commit()
        insertedColour=curr.fetchall()
        insertedColour=insertedColour[0]
        insertedColour['created_at']=insertedColour['created_at'].strftime('%Y-%m-%d %H:%M:%S')
        
        print(insertedColour)
        
        return {
             'statusCode': 200,
             'status': 'successfully Added colour group',
             'colour': json.dumps(insertedColour)
        
         }        
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'unsuccessful attempt to add colour',
             'error': json.dumps(str(e))
         }
    finally:
        if curr is not None:
            curr.close()
        if conn is not None:
            conn.close()
