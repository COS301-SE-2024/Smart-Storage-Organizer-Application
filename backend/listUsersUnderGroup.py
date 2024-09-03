import boto3
import os
import json

def lambda_handler(event, context):
    client=boto3.client('cognito-idp')
    try:
        response=client.list_users(
            UserPoolId=os.environ['USER_POOL_ID']
        )
    except client.exceptions.ClientError as error:
        return {
            'statusCode': 500,
            'body': str(error)
        }
    users=response['Users']
    filtered_users=[]
    if event['body']['type']=='Admin':
        for user in users:
           if user['Attributes']['address']==event['body']['organization_id']:
                filtered_users.append(user)
        return {
            'statusCode': 200,
            'body': filtered_users
        }
    return {
        "statusCode": 403,
        "body": 'User is not a Manager'
    }
