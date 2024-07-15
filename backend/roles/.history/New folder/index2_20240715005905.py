import boto3
import json
from botocore.exceptions import ClientError

def lambda_handler(event, context):
    # Parse the request body

    # token = "eyJraWQiOiJyQkJKVk4xNGJIcVUxbEtYSVVCeVBcL25RWTUwMmhIRWNJcnlKUWJqc0E3cz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEiLCJjb2duaXRvOmdyb3VwcyI6WyJndWVzdFVzZXIiXSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRVBiZ0lVTUVRIiwiY2xpZW50X2lkIjoiNnI2ZTcxc2V0NG1vZjJpM3R2a2d2NmluZW0iLCJvcmlnaW5fanRpIjoiOGQ2NjdiOTktMGZhZC00YTAxLWJkZmYtODgwNjAxZmU2MjgxIiwiZXZlbnRfaWQiOiI2ZjQ1MzMxOC1mMDFjLTRjZTEtYTBiNC1kNDExNDU4MDJkYWUiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIiwiYXV0aF90aW1lIjoxNzIwOTk2MjA3LCJleHAiOjE3MjA5OTk4MDcsImlhdCI6MTcyMDk5NjIwNywianRpIjoiZjc3Nzg4YjQtNTkzOC00ZjRhLTkyZGUtNWJhYTkyMGU2ZmMzIiwidXNlcm5hbWUiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEifQ.HnB9qi-ry_gPridnOGb-QqroKbZ0lc0hApyjeHe8DKjR8zdAUCRRz1l6F1XZERJDJnD_kUAjaw2l3dr5BGUuwtUTPpi6b0XItxSN3N-fxMVIfBBGpTfkcKojWdzHLmKR5TtYAgb9MWlKYnM5mNGDDbc9a0WRTW-apeTtB8Zch4HAcHMqQwmOaiSQF5x6jM80SB0L_isD5eqWdJuqDgGNklXbzI4rneSV9xjB6jqYzuFTG3kUtpGg--J20rKJQWxP79WD0t5jrUc3G-jVQVYWX486SSSgZcWSJqvpT-_FgrCCEzENlcU8uQmq3TOWufhSzligf07hmT949YK9qlVcXQ"
    token = event['body']['token']
    # Initialize Cognito IDP client
    cognito_client = boto3.client('cognito-idp')

    # Cognito User Pool ID
    user_pool_id = 'us-east-1_EPbgIUMEQ'  # Replace with your User Pool ID

   
    try:
        # Validate the token
        response = cognito_client.get_user(
            AccessToken=token
        )
        print(response)
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

 event={
    "body":{
        "token":"eyJraWQiOiJyQkJKVk4xNGJIcVUxbEtYSVVCeVBcL25RWTUwMmhIRWNJcnlKUWJqc0E3cz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEiLCJjb2duaXRvOmdyb3VwcyI6WyJndWVzdFVzZXIiXSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRVBiZ0lVTUVRIiwiY2xpZW50X2lkIjoiNnI2ZTcxc2V0NG1vZjJpM3R2a2d2NmluZW0iLCJvcmlnaW5fanRpIjoiOGQ2NjdiOTktMGZhZC00YTAxLWJkZmYtODgwNjAxZmU2MjgxIiwiZXZlbnRfaWQiOiI2ZjQ1MzMxOC1mMDFjLTRjZTEtYTBiNC1kNDExNDU4MDJkYWUiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIiwiYXV0aF90aW1lIjoxNzIwOTk2MjA3LCJleHAiOjE3MjA5OTk4MDcsImlhdCI6MTcyMDk5NjIwNywianRpIjoiZjc3Nzg4YjQtNTkzOC00ZjRhLTkyZGUtNWJhYTkyMGU2ZmMzIiwidXNlcm5hbWUiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEifQ.HnB9qi-ry_gPridnOGb-QqroKbZ0lc0hApyjeHe8DKjR8zdAUCRRz1l6F1XZERJDJnD_kUAjaw2l3dr5BGUuwtUTPpi6b0XItxSN3N-fxMVIfBBGpTfkcKojWdzHLmKR5TtYAgb9MWlKYnM5mNGDDbc9a0WRTW-apeTtB8Zch4HAcHMqQwmOaiSQF5x6jM80SB0L_isD5eqWdJuqDgGNklXbzI4rneSV9xjB6jqYzuFTG3kUtpGg--J20rKJQWxP79WD0t5jrUc3G-jVQVYWX486SSSgZcWSJqvpT-_FgrCCEzENlcU8uQmq3TOWufhSzligf07hmT949YK9qlVcXQ"
  
    }
}

context={}
print(lambda_handler(event, context))


