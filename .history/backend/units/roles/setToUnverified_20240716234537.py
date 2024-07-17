import os
import boto3

def lambda_handler(event, context):
    client = boto3.client('cognito-idp')
    responseRemove=client.admin_remove_user_from_group(
        UserPoolId='us-east-1_EPbgIUMEQ',
        Username=event['body']['username'],
        GroupName='verifiedUsers'
    )
    response = client.admin_add_user_to_group(
        UserPoolId='us-east-1_EPbgIUMEQ',
        Username=event['body']['username'],
        GroupName='unVerifiedUsers'
    )
    if response['ResponseMetadata']['HTTPStatusCode'] != 200:
        return {
            'statusCode': 400,
            'body': "User not added to unverified group"
        }
    return {
        'statusCode': 200,
        'body': "User added to unverified group"
    }

event={
  "header": {
    "Authorization": " "
  },
  "body": {
    "username": "zhouvel7@gmail.com"

  
  }
}

context={}
print(lambda_handler(event, context))