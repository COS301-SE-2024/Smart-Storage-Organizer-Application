import os
import boto3

def lambda_handler(event, context):
    client = boto3.client('cognito-idp')
    response = client.admin_list_groups_for_user(
        UserPoolId=os.environ['USER_POOL_ID'],
        Username=event['body']['username']
    )
    groups=response['Groups']
    for group in groups:
        if group['GroupName']=='verifiedUsers':
            return {
                'statusCode': 200,
                 'body': {
                    'status':"verified"
                 }
            }
    return {
        'statusCode': 200,
        'body': {
            'status':"unverified"
        }
    }

print(lambda_handler({
  "header": {
    "Authorization": " "
  },
  "body": {
    "username": "zhouvel7@gmail.com",
    "role":"guestUser"
  
    }},{} ))

