import json
import os
import sys
import boto3
import psycopg2
from psycopg2.extras import RealDictCursor


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

# body={
#     # "body":{
#     #     "previous":{
#     #         "item_id":29,
#     #         "item_name":"IPHONE 15",
#     #         "item_description":"IPHONE 15 256GB",
#     #         "item_price":1200.0,
#     #         "item_quantity":10,
#     #         "item_category":"ELECTRONICS",
#     #         "item_subcategory":"MOBILE",
#     #         "item_brand":"APPLE",
#     #         "item_model":"IPHONE 15",
#     #         "item_color":"BLACK",
#     #         "item_size":"6.1 INCH",
#     #         "item_weight":"200G",
#     #         "item_material":"ALUMINIUM"
#     #     },
#     #     "current":{"item_id":29,"item_name":"IPHONE 15","item_description":"IPHONE 15 256GB","item_price":1200.0,"item_quantity":10,"item_category":"ELECTRONICS","item_subcategory":"MOBILE","item_brand":"APPLE","item_model":"IPHONE 15","item_color":"BLACK","item_size":"6.1 INCH","item_weight":"200G","item_material":"ALUMINIUM"},
#     #     "changes":"",
#     #     "changed_by":"victor",
#     #     "changes_date_and_time":"2024-09-12 11:52:16",
#     #     "related_record_id":29,
   
#     #     "related_record_name":"IPHONE 15",
#     #     "changes_type":"EDIT",
#     #     "Reason_for_change":"WRONG SPELLING IN ITEM DESCRIPTION",
#     #     "organization_id":1,
#     #     "changes_for":"ITEM",
#     #     "comments":"",
#     #     "username":"zhouvel@gmail.com",
        



#     # }
# }

def lambda_handler(event,context):
    body=event['body']
    if isinstance(body, str):
        body = json.loads(body)
    if 'username' not in body:
        return {
            'statusCode': 400,
            'body': json.dumps('Invalid Request')
        }
    role=check_access(body['username'])
    if role['statusCode']!=200:
        return role
    user_role=role['body']['role']
    if user_role not in ['Admin','Manager','normalUser']:
        return {
            'statusCode': 403,
            'body': json.dumps('Forbidden')
        }

    if 'changes' not in body or 'changes_date_and_time' not in body or 'changed_by' not in body or 'related_record_id' not in body or 'changes_type' not in body or 'related_record_name' not in body or 'changes_type' not in body or 'Reason_for_change' not in body or 'organization_id' not in body or 'previous' not in body or 'current' not in body:
        return {
            'statusCode': 400,
            'body': json.dumps('Invalid Request')
        }
    
    type=body['changes_type']
    if type not in ['EDIT','DELETE','ADD']:
        return {
            'statusCode': 400,
            'body': json.dumps('Invalid Request')
        }
    con=get_db_connection()
    cur=con.cursor(cursor_factory=RealDictCursor)
    try:
        query='INSERT INTO changes_made (change_description,change_date,changed_by,related_record_id,changes_type,related_record_name,change_type,reason_for_change,organization_id,previous,current,comments) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
        cur.execute(query,(body['changes'],body['changes_date_and_time'],body['changed_by'],body['related_record_id'],body['changes_for'],body['related_record_name'],body['changes_type'],body['Reason_for_change'],body['organization_id'], json.dumps(body['previous']), json.dumps(body['current']),body['comments']))
        con.commit()
    except Exception as e:
        print(e)
        return {
            'statusCode': 500,
            'body': json.dumps('Internal Server Error')
        }
    finally:
        con.close()
        return {
            'statusCode': 200,
            'body': json.dumps('Changes saved successfully')
        }


print(lambda_handler(body,{}))