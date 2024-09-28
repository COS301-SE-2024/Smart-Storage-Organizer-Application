import boto3
import os
import json

client = boto3.client('cognito-idp', region_name='us-east-1')

def get_user_role(username):
    


    
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=os.environ.get('USER_POOL_ID'),
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
        return {
            "statusCode": 200,
            "body": group_name
        }
    except client.exceptions.UserNotFoundException:
        return {
            "statusCode": 403,
            "body": "User Not Found"
        }
    except client.exceptions.ClientError as error:
        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }

def lambda_handler(event, context):
    if 'body' not in event:
        return {
            'statusCode': 400,
            'body': 'Missing request body'
        }
    body=event['body']
    if isinstance(body, str):
        try:
            body = json.loads(body)
        except json.JSONDecodeError as error:
            return {
                'statusCode': 400,
                'body': str(error)
            }
    user_role=get_user_role(body['username'])
    if(user_role['statusCode']!=200):
        return user_role
    print(user_role)
    if user_role['body']!='Manager' and user_role['body']!='Admin':
        return {
            'statusCode': 403,
            'body': 'User is not a Manager or Admin'
        }

    client=boto3.client('cognito-idp')
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=os.getenv('USER_POOL_ID'),
            Username=event['body']['username']
        )
    except client.exceptions.ClientError as error:
        return {
            'statusCode': 500,
            'body': str(error)
        }
    groups=response['Groups']
    for  group in groups:
        if group['GroupName']=='Manager':
            try:
                response=client.list_users_in_group(
                    UserPoolId=os.getenv('USER_POOL_ID'),
                    GroupName=event['body']['type']
                )
                simplified_users = []
                # print(response)
                for user in response['Users']:
                    user_info = {'email': None, 'name': None, 'surname': None, 'organization_id': None}
                    for attribute in user['Attributes']:
                        if attribute['Name'] == 'email' :
                            user_info['email'] = attribute['Value']
                        elif attribute['Name'] == 'name':
                            user_info['name'] = attribute['Value']
                        elif attribute['Name'] == 'family_name':
                            user_info['surname'] = attribute['Value']
                        elif attribute['Name'] == 'address':
                            user_info['organization_id'] = attribute['Value']
                    if  str(user_info['organization_id'])==event['body']['organization_id']:
                        simplified_users.append(user_info)
                        

                return {
                    'statusCode': 200,
                    'body': simplified_users
                }
            except client.exceptions.ClientError as error:
                return {
                    'statusCode': 500,
                    'body': str(error)
                }
    return {
        "statusCode": 403,
        "body": 'User is not a Manager'
    }

