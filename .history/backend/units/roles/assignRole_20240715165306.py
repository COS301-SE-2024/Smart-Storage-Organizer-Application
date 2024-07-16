import boto3

cognito_idp = boto3.client('cognito-idp')

def assign_role(event):
    cognito_idp.admin_add_user_to_group(
        UserPoolId='us-east-1_EPbgIUMEQ',
        Username=event['username'],
        GroupName=event['role']
    )

def lambda_handler(event, context):
    try:
        assign_role(event)
    except Exception as e:
        return {
            'statusCode': 500,
            'body': 'An error occurred while assigning the user to the Admin group'
        }

    return {
        'statusCode': 200,
        'body': 'User has been assigned to the Admin group'
    }

event={
    'username':"zhouvel@gmail.com",
    'role':"Admin"
}
context={}

print(lambda_handler(event, context))