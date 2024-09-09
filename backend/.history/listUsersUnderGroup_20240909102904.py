import boto3
import os
import json
import datetime


client = boto3.client('cognito-idp')
user_pool_id = 'us-east-1_EPbgIUMEQ'

def get_user_role(username):
   
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups=response['Groups']
   
    
        return{
            "statusCode": 200,
            "body":groups
        
        }
    except client.exceptions.ClientError as error:
        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }


def lambda_handler(event, context):


    roles = get_user_role(event['body']['username'])
    group_names = [roles['body']]  # Extract the role directly from the dictionary
    print(group_names[0])
    group = [group['GroupName'] for group in group_names[0]]
        
        
    client=boto3.client('cognito-idp')
    try:
        response = client.list_users_in_group(
            UserPoolId='us-east-1_EPbgIUMEQ',
            GroupName=event['body']['Group'],
        )
    except client.exceptions.ClientError as error:
        return {
            'statusCode': 500,
            'body': str(error)
        }
    users=response['Users']
    filtered_users=[]


    
    for user in users:
        for attribute in user['Attributes']:
            if attribute['Name'] == 'address' and attribute['Value'] == event['body']['organization_id']:
                user_info = {}
                for attr in user['Attributes']:
                    if attr['Name'] in ['email', 'name', 'family_name']:
                        user_info[attr['Name']] = attr['Value']
                filtered_users.append(user_info)
                break
    for user in filtered_users:
        for key, value in user.items():
            if isinstance(value, datetime.datetime):
                user[key] = value.isoformat()
    return {
        'statusCode': 200,
        'body': "hello"
    }
    return {
        "statusCode": 403,
        "body": 'User is not a Manager'
    }

event={'body': {'username': 'zhouvel7@gmail.com',
                'Group':'Admin',
                'organization_id': 'Makro'}}
context={}
print(lambda_handler(event, context))