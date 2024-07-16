import boto3
import os

client = boto3.client('cognito-idp')
user_pool_id = os.environ['USER_POOL_ID']

def getUserRole(event):
    body= event['body']
    username= body['username']
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups = [group['GroupName'] for group in response['Groups']]
        return groups
    except client.exceptions.ClientError as error:
        print(f"Error listing groups for user: {error}")
        return []
        