import boto3
import os

client = boto3.client('cognito-idp')
user_pool_id = 'us-east-1_EPbgIUMEQ'

def getUserRole(event):
    body= event['body']
    username= body['username']
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        return response
        #groups = responce['Groups']
        groups = [group for group in response['Groups']]
        return {
            'statusCode': 200,
            body: groups
        }
    except client.exceptions.ClientError as error:
        print(f"Error listing groups for user: {error}")
        return {
            'statusCode': 500,
            body: 'Internal Server Error'
        }

event={
    "body": {
        "username": "zhouvel7@gmail.com",
    }

}
context={}
print(getUserRole(event))