import boto3

cognito_idp = boto3.client('cognito-idp')
assign_role={}
cognito_idp.admin_add_user_to_group(
    UserPoolId='us-east-1_EPbgIUMEQ',
    Username='zhouvel7@gmail.com',
    GroupName='Admin'
)

def lambda_handler(event, context):
    try:

    return {
        'statusCode': 200,
        'body': 'User has been assigned to the Admin group'
    }