import boto3
import json
from botocore.exceptions import ClientError

def lambda_handler(event, context):
    # Parse the request body
    body = event['body']
    token = body.get('token')  # Expecting the JWT token from the client

    # Initialize Cognito IDP client
    cognito_client = boto3.client('cognito-idp')

    # Cognito User Pool ID
    user_pool_id = 'us-east-1_EPbgIUMEQ'  # Replace with your User Pool ID

    try:
        # Validate the token
        response = cognito_client.get_user(
            AccessToken=token
        )
    except ClientError as e:
     return {
        'statusCode': 400,
        'body': json.dumps(f'Error validating token: {str(e)}')
    }

