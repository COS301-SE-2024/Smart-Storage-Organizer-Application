import boto3
import os
cognito_idp = boto3.client('cognito-idp')
user_pool_id = os.environ['USER_POOL_ID']

def list_user_groups(user_pool_id, username):
    try:
        response = cognito_idp.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups = [group['GroupName'] for group in response['Groups']]
        return groups
    except cognito_idp.exceptions.ClientError as error:
        print(f"Error listing groups for user: {error}")
        return []
def assign_role(event):
   
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
            'body': 'User has been assigned to the '+event['role']+' group'
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