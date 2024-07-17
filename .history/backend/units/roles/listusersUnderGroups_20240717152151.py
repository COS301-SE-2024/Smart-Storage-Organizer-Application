import boto3
import os
import json

def lambda_handler(event, context):
    client=boto3.client('cognito-idp')
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId='us-east-1_EPbgIUMEQ',
            Username=event['body']['username']
        )
    except client.exceptions.ClientError as error:
        print(f"Error listing groups for user: {error}")
        return {
            'statusCode': 500,
            'body': str(error)
        }
    groups=response['Groups']
    for  group in groups:
        if group['GroupName']=='Manager':
            try:
                response=client.list_users_in_group(
                    UserPoolId='us-east-1_EPbgIUMEQ',
                    GroupName=event['body']['type']
                )
                simplified_users = []

                for user in response['Users']:
                    user_info = {'email': None, 'name': None, 'surname': None}
                    for attribute in user['Attributes']:
                        if attribute['Name'] == 'email':
                            user_info['email'] = attribute['Value']
                        elif attribute['Name'] == 'name':
                            user_info['name'] = attribute['Value']
                        elif attribute['Name'] == 'family_name':
                            user_info['surname'] = attribute['Value']
                    simplified_users.append(user_info)
                return {
                    'statusCode': 200,
                    'body': json.dumps(response['Users'])
                }
            except client.exceptions.ClientError as error:
                print(f"Error listing users in group: {error}")
                return {
                    'statusCode': 500,
                    'body': str(error)
                }
    return {
        "statusCode": 403,
        "body": 'User is not a Manager'
    }

event={
    "body": {
        "username": "zhouvel7@gmail.com",
        "type": "Admin"
    }
}
context={}
print(lambda_handler(event, context))