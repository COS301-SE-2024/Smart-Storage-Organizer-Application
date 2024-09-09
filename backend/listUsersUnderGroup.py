import boto3
import os
import json
import datetime
def lambda_handler(event, context):
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
    if event['body']['AccessLevel']=='Admin':
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
            'body': filtered_users
        }
    return {
        "statusCode": 403,
        "body": 'User is not a Manager'
    }

event={'body': {'AccessLevel': 'Admin',
                'Group':'Admin',
                'organization_id': 'Makro'}}
context={}
print(lambda_handler(event, context))