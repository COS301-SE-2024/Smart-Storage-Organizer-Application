import json
import boto3
import psycopg2
from psycopg2.extras import RealDictCursor
import os

client = boto3.client('cognito-idp')
user_pool_id = 'us-east-1_EPbgIUMEQ'


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
        return "user_role_placeholder"

def get_db_connection():
    try:
        con = psycopg2.connect(
            host='Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com',
            database='postgres',
            user='MasterUser',
            password='MasterDb#ss1',
            port = '5432'
         )
        return con
    except Exception as e:
        print(f"Error connecting to the database: {e}")
        raise
    
def search_unit(conn,curr,name):
    query="SELECT * FROM UNITS WHERE Unit_Name = %s"
    curr.execute(query, (name,))
    conn.commit()
    return curr.fetchall()

def add_constraints(constraint,unit_id,curr,conn):
    constraint_list=constraint.split(',')
    for i in constraint_list:
        query="INSERT INTO unit_category_constraint (category_id,unit_ID) VALUES (%s,%s)"
        parameters = (i,unit_id)
        curr.execute(query, parameters)
        conn.commit()
    return {
        'statusCode': 200,
        'body': 'Unit Added'
    }

def add_unit(event, context):
    conn = None
    curr = None
    try:
        if(search_unit(conn,curr,event['Unit_Name'])):
            return {
                'statusCode': 400,
                'body': 'Unit already exists'
            }
        else:
            query="INSERT INTO UNITS (Unit_Name,Unit_Capacity,Unit_QR,unit_capacity_used) VALUES (%s,%s,%s,%s)"
            parameters = (event['body']['Unit_Name'], event['body']['Unit_Capacity'], event['body']['Unit_QR'],event['body']['unit_capacity_used'])
            curr.execute(query, parameters)
            conn.commit()
            new_id=search_unit(conn,curr,event['Unit_Name'])[0]['unit_id']
            add_constraints(event['constraints'],new_id,curr,conn)
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
    conn = None
    curr = None
    try:
        user_role = get_user_role(event)
        if user_role == 'guestUser':
            return {
                "statusCode": 403,
                "body": "You are not authorized to perform this action"
            }
        
        conn = get_db_connection()
        curr = conn.cursor(cursor_factory = RealDictCursor)

    
        response = {
            'statusCode': 200,
            'body': json.dumps('Success')
        }
    except Exception as e:
        if conn:
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
    return response

event={
    'body':json.dumps({
        'username':'bonganizungu889@gmail.com',
        'Unit_Name':'Unit 1',
        'Unit_Capacity':100,
        'Unit_QR':'QR1',
        'unit_capacity_used':1
    })
}
print(lambda_handler(event, None))

# event={"Unit_Name":"Unit 5","Unit_Capacity":100,"Unit_QR":"QR1","constraints":"1,2,3,4", "unit_capacity_used":0}

# context={}
# print(lambda_handler(event, context))