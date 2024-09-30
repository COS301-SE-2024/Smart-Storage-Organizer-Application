import boto3
import os
cognito_idp = boto3.client('cognito-idp')

def set_to_verified(event):
    try:
        responseRemove=cognito_idp.admin_remove_user_from_group(
            UserPoolId=os.environ['USER_POOL_ID'],
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
            'body':str(error)
        }
    try:
        response = cognito_idp.admin_add_user_to_group(
            UserPoolId=os.environ['USER_POOL_ID'],
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
        'body': "User not added to verified group"+str(error)
    }

def lambda_handler(event, context):
    if 'username' not in event['body']:
        return {
            'statusCode': 400,
            'body': 'Missing required fields'
        }
    return set_to_verified(event)


