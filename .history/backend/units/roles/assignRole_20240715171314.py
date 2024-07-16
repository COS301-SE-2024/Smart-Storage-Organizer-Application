import boto3

cognito_idp = boto3.client('cognito-idp')
import os
def assign_role(event):
    user_pool_id = os.environ['USER_POOL_ID']
    cognito_idp.admin_add_user_to_group(
        UserPoolId=user_pool_id,
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
            'body': str(e)
            }


# event={
#     'username':"zhouvel7@gmail.com",
#     'role':"Admint"
# }
# context={}

# print(lambda_handler(event, context))