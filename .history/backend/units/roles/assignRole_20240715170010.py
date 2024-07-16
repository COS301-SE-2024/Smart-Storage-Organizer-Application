import boto3

cognito_idp = boto3.client('cognito-idp')

def assign_role(event):
    cognito_idp.admin_add_user_to_group(
        UserPoolId='us-east-1_EPbgIUMEQ',
        Username=event['username'],
        GroupName=event['role']
    )

def lambda_handler(event, context):
    if 'username' not in event or 'role' not in event:
        return {
            'statusCode': 400,
            'body': 'Missing required fields'
        }
    if event['role'] not in ['Admin', 'normalUser', 'Manager', 'guestUser']:
        return {
            'statusCode': 400,
            'body': 'Invalid role'
        }
    try:
            assign_role(event)
            return {
            'statusCode': 200,
            'body': 'User has been assigned to the Admin group'
         }
    except Exception as e:
            return {
            'statusCode': 500,
            'body': 'An error occurred while assigning the user to the Admin group'
            }


event={
    'username':"zhouvel7@gmail.com",
    'role':"Admin"
}
context={}

print(lambda_handler(event, context))