import boto3
import json

def lambda_handler(event, context):
    # Initialize the STS client
    sts_client = boto3.client('sts')

    # Map user types to roles
    user_roles = {
        'admin': 'arn:aws:iam::123456789012:role/AdminRole',
        'manager': 'arn:aws:iam::123456789012:role/ManagerRole',
        'normal': 'arn:aws:iam::123456789012:role/NormalRole',
        'guest': 'arn:aws:iam::123456789012:role/GuestRole'
    }

    # Get the user type from the event
    user_type = event.get('user_type')

    if user_type not in user_roles:
        return {
            'statusCode': 400,
            'body': json.dumps('Invalid user type')
        }

    # Assume the role for the user type
    role_arn = user_roles[user_type]
    assumed_role = sts_client.assume_role(
        RoleArn=role_arn,
        RoleSessionName='UserSession'
    )

    # Extract the temporary credentials
    credentials = assumed_role['Credentials']

    # Use the assumed role credentials to perform actions
    # Example: Creating an S3 client with the assumed role credentials
    s3_client = boto3.client(
        's3',
        aws_access_key_id=credentials['AccessKeyId'],
        aws_secret_access_key=credentials['SecretAccessKey'],
        aws_session_token=credentials['SessionToken']
    )

    # Example: Listing buckets
    response = s3_client.list_buckets()

    return {
        'statusCode': 200,
        'body': json.dumps(response)
    }