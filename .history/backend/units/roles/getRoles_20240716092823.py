import boto3
import os
import json

client = boto3.client('cognito-idp')
user_pool_id = 'us-east-1_EPbgIUMEQ'

def get_user_role(event):
    body= event['body']
    username= body['username']
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        print(response)
        groups=response['Groups'][0]
        print(groups)
        group_name=groups['GroupName']
        print(group_name)
        return{
            "statusCode": 200,
            "body":{
                "role": json.dumps(group_name)
            }
        }
    except client.exceptions.ClientError as error:
        print(f"Error listing groups for user: {error}")
        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }
def lambda_handler(event, context):
    return get_user_role(event)
event={
    "body": {
        "username": "zhouvel7@gmail.com",
    }

}
context={}
print(getUserRole(event))