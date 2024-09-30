import boto3
import os
import json
cognito_idp = boto3.client('cognito-idp')
user_pool_id = os.environ.get('USER_POOL_ID')

def list_user_groups(user_pool_id, username):
    try:
        response = cognito_idp.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups = [group['GroupName'] for group in response['Groups']]
        return groups
    except cognito_idp.exceptions.ClientError as error:
        print(f"Error listing groups for user: {error}")
        return []
def assign_role(event):
   
    cognito_idp.admin_add_user_to_group(
        UserPoolId=user_pool_id,
        Username=event['body']['email'],
        GroupName=event['body']['role']
    )
client = boto3.client('cognito-idp', region_name='us-east-1')

def get_user_role(username):
    


    
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
    try:
            username = event['body']['username']
            user_groups = list_user_groups(user_pool_id, username)
            print(f"User {username} is in groups: {user_groups}")
            if event['body']['role'] in user_groups:
                return {
                    'statusCode': 409,
                    'body': 'User is already in the '+event['body']['role']+' group'
                }
            else :
                 for group in user_groups:
                        try:
                            cognito_idp.admin_remove_user_from_group(
                                UserPoolId=user_pool_id,
                                Username=username,
                                GroupName=group
                            )
                            print(f"Removed {username} from {group}")
                        except cognito_idp.exceptions.ClientError as error:
                            print(f"Error removing {username} from {group}: {error}")
            
            assign_role(event)
            return {
            'statusCode': 200,
            'body': 'User has been assigned to the '+event['body']['role']+' group'
         }
    except Exception as e:
            return {
            'statusCode': 500,
            'body': str(e)
            }


