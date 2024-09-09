import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os

username=os.getenv('DBUsername')
passcode=os.getenv('DBPassword')
hostadress=os.getenv('DBHost')
DBname=os.getenv('DBName')
con = None

def get_user_role(event):
    body = event['body']
    username = body['username']
    try:
        response = client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups = response['Groups'][0]
        group_name = groups['GroupName']
        return group_name
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
    if user_role is None:
        return {
            "statusCode": 403,
            "body": "User role not found or unauthorized"
        }

    if user_role is 'guestUser':
        return {
            "statusCode": 403,
            "body": "You are not authorized to perform this action"
        }
    
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:    
        SQLQuery= 'UPDATE category SET categoryname = %s where id = %s and organizationid=%s;'
        parameters = (event['categoryname'], event['id'], event['organizationid'])
        curr.execute(SQLQuery, parameters)
        conn.commit()
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'Error modifying category name',
             'error': json.dumps(str(e))
         }
    finally:
        curr.close()
        conn.close()
       
    return {
            'statusCode': 200,
            'body': 'category changed successfully'
        }
       

    

