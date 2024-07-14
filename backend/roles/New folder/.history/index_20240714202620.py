import requests
import json
import jwt

def lambda_handler(event, context):
    try:
        # Existing setup code...
        cognito_region = 'us-east-1'
        user_pool_id = 'us-east-1_EPbgIUMEQ'
        keys_url = f'https://cognito-idp.{cognito_region}.amazonaws.com/{user_pool_id}/.well-known/jwks.json'
        response = requests.get(keys_url)
        keys = response.json()['keys']
        
        # Assuming unverified_header and id_token are defined earlier in the code
        key = [k for k in keys if k['kid'] == unverified_header['kid']][0]
        public_key = jwt.algorithms.RSAAlgorithm.from_jwk(json.dumps(key))
        decoded = jwt.decode(id_token, public_key, algorithms=['RS256'], audience="6r6e71set4mof2i3tvkgv6inem")
        
        # Check if the user belongs to a specific group
        user_groups = decoded.get('cognito:groups', [])
        
        if 'Admin' in user_groups:
            # User is an admin
            return {
                'statusCode': 200,
                'body': json.dumps({"message": "User is an admin", "groups": user_groups})
            }
        else:
            # User is not an admin
            return {
                'statusCode': 403,
                'body': json.dumps({"message": "User is not an admin", "groups": user_groups})
            }
    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({"error": str(e)})
        }

# Assuming event and context are defined or passed appropriately
event = {}
context = {}
print(lambda_handler(event, context))