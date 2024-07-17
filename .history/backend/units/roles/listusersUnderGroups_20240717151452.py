import boto3
import os

def lambda_handler(event, context):
    client=boto3.client('cognito-idp')
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId='us-east-1_EPbgIUMEQ',
            Username=event['body']['username']
        )
    except client.exceptions.ClientError as error:
        print(f"Error listing groups for user: {error}")
        return {
            'statusCode': 500,
            'body': str(error)
        }
    groups=response['Groups']
    for  group in groups:
        if group['GroupName']=='Manager':
            try:
                response=client.list_users_in_group(
                    UserPoolId='us-east-1_EPbgIUMEQ',
                    GroupName=event['body']['type']
                )
                return {
                    'statusCode': 200,
                    'body': json.dumps(response['Users'])
                }
            except client.exceptions.ClientError as error:
                print(f"Error listing users in group: {error}")
                return {
                    'statusCode': 500,
                    'body': str(error)
                }
    return {
        "statusCode": 403,
        "body": 'User is not a Manager'
    }

event={
    "body": {
        "username": "zhouvel7@gmail.com",
        "type": "Admin"
    }
}
context={}
print(lambda_handler(event, context))