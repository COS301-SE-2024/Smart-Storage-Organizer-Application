import os
import boto3

def lambda_handler(event, context):
    client = boto3.client('cognito-idp')
    response = client.admin_add_user_to_group(
        UserPoolId=os.environ['USER_POOL_ID'],
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

