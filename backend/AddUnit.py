import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os
import boto3
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

def check_access(username):
    try:
        client = boto3.client('cognito-idp',region_name='us-east-1')
        response=client.admin_list_groups_for_user(
                UserPoolId=os.getenv('USER_POOL_ID'),
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

def search_unit(conn,curr,name):
    query="SELECT * FROM UNITS WHERE Unit_Name = %s"
    curr.execute(query, (name,))
    conn.commit()
    return curr.fetchall()

def add_constraints(constraint,unit_id,organization_id,curr,conn):
    try:
        constraint_list=constraint.split(',')
        for i in constraint_list:
            query="INSERT INTO unit_category_constraint (category_id,unit_ID,organization_id) VALUES (%s,%s,%s)"
            parameters = (i,unit_id,organization_id)
            curr.execute(query, parameters)
            conn.commit()
        return {
        'statusCode': 200,
        'body': 'Unit Added'
    }
    except Exception as e:
        conn.rollback()
        return {
            'statusCode': 500,
            'body': 'Error adding item',
            'error': json.dumps(str(e))
        }

def add_unit(conn,curr,body):
    for key in ['Unit_Name','Unit_Capacity','Unit_QR','unit_capacity_used','organization_id','constraints']:
        if key not in body:
            return {
                'statusCode': 400,
                'body': json.dumps('Bad Request')
            }
    
    try:
        if(search_unit(conn,curr,body['Unit_Name'])):
            return {
                'statusCode': 400,
                'body': 'Unit already exists'
            }
        else:
            query="INSERT INTO UNITS (Unit_Name,Unit_Capacity,Unit_QR,unit_capacity_used,organizationid) VALUES (%s,%s,%s,%s,%s)"
            parameters = (body['Unit_Name'], body['Unit_Capacity'], body['Unit_QR'],body['unit_capacity_used'],body['organization_id'])
            curr.execute(query, parameters)
            conn.commit()
            new_id=search_unit(conn,curr,body['Unit_Name'])[0]['unit_id']
            response =add_constraints(body['constraints'],new_id,body['organization_id'],curr,conn)
            if response['statusCode']!=200:
                query="DELETE FROM UNITS WHERE unit_name = %s"
                curr.execute(query, (body['Unit_Name'],))
                conn.commit()
                return response
            return {
                'statusCode': 200,
                'body': 'Unit Added'
            }
    except Exception as e:
        conn.rollback()
        return {
            'statusCode': 500,
            'body': 'Error adding item',
            'error': json.dumps(str(e))
        }
    
def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)
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
    try:
        response=add_unit(conn,curr,body)
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

