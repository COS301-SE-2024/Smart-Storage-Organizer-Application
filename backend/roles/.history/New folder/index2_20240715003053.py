import boto3
import json
from botocore.exceptions import ClientError

def lambda_handler(event, context):
    # Parse the request body
    body = event['body']
    token = "eyJraWQiOiJyQkJKVk4xNGJIcVUxbEtYSVVCeVBcL25RWTUwMmhIRWNJcnlKUWJqc0E3cz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEiLCJjb2duaXRvOmdyb3VwcyI6WyJndWVzdFVzZXIiXSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRVBiZ0lVTUVRIiwiY2xpZW50X2lkIjoiNnI2ZTcxc2V0NG1vZjJpM3R2a2d2NmluZW0iLCJvcmlnaW5fanRpIjoiOGQ2NjdiOTktMGZhZC00YTAxLWJkZmYtODgwNjAxZmU2MjgxIiwiZXZlbnRfaWQiOiI2ZjQ1MzMxOC1mMDFjLTRjZTEtYTBiNC1kNDExNDU4MDJkYWUiLCJ0b2tlbl91c2UiOiJhY2Nlc3MiLCJzY29wZSI6ImF3cy5jb2duaXRvLnNpZ25pbi51c2VyLmFkbWluIiwiYXV0aF90aW1lIjoxNzIwOTk2MjA3LCJleHAiOjE3MjA5OTk4MDcsImlhdCI6MTcyMDk5NjIwNywianRpIjoiZjc3Nzg4YjQtNTkzOC00ZjRhLTkyZGUtNWJhYTkyMGU2ZmMzIiwidXNlcm5hbWUiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEifQ.HnB9qi-ry_gPridnOGb-QqroKbZ0lc0hApyjeHe8DKjR8zdAUCRRz1l6F1XZERJDJnD_kUAjaw2l3dr5BGUuwtUTPpi6b0XItxSN3N-fxMVIfBBGpTfkcKojWdzHLmKR5TtYAgb9MWlKYnM5mNGDDbc9a0WRTW-apeTtB8Zch4HAcHMqQwmOaiSQF5x6jM80SB0L_isD5eqWdJuqDgGNklXbzI4rneSV9xjB6jqYzuFTG3kUtpGg--J20rKJQWxP79WD0t5jrUc3G-jVQVYWX486SSSgZcWSJqvpT-_FgrCCEzENlcU8uQmq3TOWufhSzligf07hmT949YK9qlVcXQ"

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

