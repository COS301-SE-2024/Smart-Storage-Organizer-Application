import boto3
import os
cognito_idp = boto3.client('cognito-idp')

def set_to_verified(event):
    try:
        responseRemove=cognito_idp.admin_remove_user_from_group(
            UserPoolId='us-east-1_EPbgIUMEQ',
            Username=event['body']['username'],
            GroupName='unVerifiedUsers'
        )
        if responseRemove['ResponseMetadata']['HTTPStatusCode'] != 200:
            return {
                'statusCode': 400,
                'body': "User not removed from unverified group"
            }
    except cognito_idp.exceptions.ClientError as error:
        return {
            'statusCode': 400,
            'body': "User not removed from unverified group"
        }
    try:
        response = cognito_idp.admin_add_user_to_group(
            UserPoolId='us-east-1_EPbgIUMEQ',
            Username=event['body']['username'],
            GroupName='verifiedUsers'
            )
        if response['ResponseMetadata']['HTTPStatusCode'] != 200:
            return {
                'statusCode': 400,
                'body': "User not added to verified group"
            }
        return {
            'statusCode': 200,
            'body': "User added to verified group"
        }
    except cognito_idp.exceptions.ClientError as error:
        return {
            'statusCode': 400,
        'body': "User not added to verified group"
    }

def lambda_handler(event, context):
    if 'username' not in event['body']:
        return {
            'statusCode': 400,
            'body': 'Missing required fields'
        }
    return set_to_verified(event)

event={
    "body": {
        "username": "zhouvel7@gmal.com"
    }
}
context={}
print(lambda_handler(event, context))