import boto3

cognito_idp = boto3.client('cognito-idp')

cognito_idp.admin_add_user_to_group(
    UserPoolId='us-east-1_EPbgIUMEQ',
    Username='username',
    GroupName='group_name'
)