import boto3
import json
from botocore.exceptions import ClientError

def lambda_handler(event, context):
    # Parse the request body
    body = json.loads(event['body'])
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

        username = response['Username']
        user_attributes = response['UserAttributes']

        # Process user attributes as needed
        # For example, you can extract the email or other attributes
        email = next(attr['Value'] for attr in user_attributes if attr['Name'] == 'email')

        # Map user roles based on attributes or custom claims in the token
        # You may need to adapt this mapping based on your use case
        user_roles = {
            'admin@example.com': 'arn:aws:iam::123456789012:role/AdminRole',
            'manager@example.com': 'arn:aws:iam::123456789012:role/ManagerRole',
            'user@example.com': 'arn:aws:iam::123456789012:role/NormalRole',
            # Add other mappings as needed
        }

        # Determine user role
        user_type = None
        for key, value in user_roles.items():
            if email == key:
                user_type = value
                break

        if user_type is None:
            return {
                'statusCode': 403,
                'body': json.dumps('User not authorized')
            }

        # Assume the role
        sts_client = boto3.client('sts')
        assumed_role = sts_client.assume_role(
            RoleArn=user_type,
            RoleSessionName=f'{username}_Session'
        )

        credentials = assumed_role['Credentials']

        # Return temporary credentials
        response = {
            'AccessKeyId': credentials['AccessKeyId'],
            'SecretAccessKey': credentials['SecretAccessKey'],
            'SessionToken': credentials['SessionToken']
        }

        return {
            'statusCode': 200,
            'body': json.dumps(response)
        }

    except ClientError as e:
        return {
            'statusCode': 400,
            'body': json.dumps(f'Error validating token: {str(e)}')
        }
