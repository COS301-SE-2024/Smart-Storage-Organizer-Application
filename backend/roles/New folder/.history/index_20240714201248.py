import jwt
import boto3


def lambda_handler(event, context):
    # Assuming the ID token is passed in the event
    id_token = event['headers']['Authorization']
    # Decode the token without verification (to extract the kid)
    unverified_header = jwt.get_unverified_header(id_token)
    
    # Get the public key from Cognito
    cognito_region = 'us-east-1'
    user_pool_id = 'your_user_pool_id'
    keys_url = f'https://cognito-idp.{cognito_region}.amazonaws.com/{user_pool_id}/.well-known/jwks.json'
    response = requests.get(keys_url)
    keys = response.json()['keys']
    
    # Find the key by kid
    key = [k for k in keys if k['kid'] == unverified_header['kid']][0]
    public_key = RSAAlgorithm.from_jwk(json.dumps(key))
    
    # Verify the token
    decoded = jwt.decode(id_token, public_key, algorithms=['RS256'], audience='your_app_client_id')
    
    # Check if the user belongs to a specific group
    user_groups = decoded.get('cognito:groups', [])
    if 'Admin' in user_groups:
        # User is an admin
        pass
    else:
        # User is not an admin
        pass