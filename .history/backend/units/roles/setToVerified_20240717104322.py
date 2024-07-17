import boto3
import os
cognito_idp = boto3.client('cognito-idp')

def set_to_verified(event):
        # responseRemove=cognito_idp.admin_remove_user_from_group(
        #     UserPoolId=os.environ['USER_POOL_ID'],
        #     Username=event['body']['username'],
        #     GroupName='unVerifiedUsers'
        # )
        # if responseRemove['ResponseMetadata']['HTTPStatusCode'] != 200:
        #     return {
        #         'statusCode': 400,
        #         'body': "User not removed from unverified group"
        #     }

        # response = cognito_idp.admin_add_user_to_group(
        #     UserPoolId=os.environ['USER_POOL_ID'],
        #     Username=event['body']['username'],
        #     GroupName='verifiedUsers'
        #     )
        # if response['ResponseMetadata']['HTTPStatusCode'] != 200:
        #     return {
        #         'statusCode': 400,
        #         'body': "User not added to verified group"
        #     }
        responce=cognito_idp.admin_get_user(
            UserPoolId='us-east-1_EPbgIUMEQ',
            Username=event['body']['username']
        )
        custom_attributes = {attr['Name']: attr['Value'] for attr in respone['UserAttributes'] if attr['Name'].startswith('custom:')}

        print("Custom Attributes:", custom_attributes)
        return {
            'statusCode': 200,
            'body': "User added to verified group"
        }

def lambda_handler(event, context):
    if 'username' not in event['body']:
        return {
            'statusCode': 400,
            'body': 'Missing required fields'
        }
    return set_to_verified(event)

event={
    "Authorization": " ",
    "body": {
        "username": "zhouvel7@gmail.com"
    }
}

context={}
print(lambda_handler(event, context))