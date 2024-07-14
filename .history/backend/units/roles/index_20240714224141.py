import json
import jwt
import requests


def lambda_handler(event, context):
    try:
        # Replace 'your_user_pool_id' and 'your_app_client_id' with actual values
        cognito_region = 'us-east-1'
        user_pool_id = 'your_user_pool_id'
        app_client_id = 'your_app_client_id'
        
        # Assuming the ID token is correctly passed in the event
        id_token = event['headers']['Authorization']
        
        # Decode the token without verification to extract the kid
        unverified_header = jwt.get_unverified_header(id_token)
       
        # Fetch the JWKS keys from Cognito
        keys_url = f'https://cognito-idp.{cognito_region}.amazonaws.com/{user_pool_id}/.well-known/jwks.json'
        response = requests.get(keys_url)
        keys = response.json()['keys']
        
        # Find the key by kid
        key = next(k for k in keys if k['kid'] == unverified_header['kid'])
        public_key = RSAAlgorithm.from_jwk(json.dumps(key))
        
        # Verify the token
        decoded = jwt.decode(id_token, public_key, algorithms=['RS256'], audience=app_client_id)
        
        # Check if the user belongs to a specific group
        user_groups = decoded.get('cognito:groups', [])
        if 'Admin' in user_groups:
            return {'statusCode': 200, 'body': json.dumps('User is an admin')}
        else:
            return {'statusCode': 200, 'body': json.dumps('User is not an admin')}
    except Exception as e:
        # Log the error and return a failure response
        print(e)
        return {'statusCode': 500, 'body': json.dumps('Error processing your request')}

event={
    "headers": {
        "Authorization": 'eyJraW'
}
}
context={}
print(lambda_handler(event, context))