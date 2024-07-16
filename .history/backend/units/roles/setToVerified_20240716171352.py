import boto3
import os
cognito_idp = boto3.client('cognito-idp')

def set_to_verified(event):
    responseRemove=cognito_idp.admin_remove_user_from_group(
        UserPoolId=os.environ['USER_POOL_ID'],
        Username=event['body']['username'],
        GroupName='unVerifiedUsers'
    )
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