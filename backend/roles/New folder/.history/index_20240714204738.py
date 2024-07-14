import jwt
import json
import boto3
import requests

def lambda_handler(event, context):
    # Assuming the ID token is passed in the event
    id_token = 'eyJraWQiOiJDS2d3cFp2dGNVT0pVbjF2MGtxUEZ0VlgyQVFLM0N2NGtmMzRUS2R6endrPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEiLCJjb2duaXRvOmdyb3VwcyI6WyJBZG1pbiJdLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRVBiZ0lVTUVRIiwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjpmYWxzZSwiY29nbml0bzp1c2VybmFtZSI6ImI0ZTg5NDI4LTcwNTEtNzA2ZS04MTdkLTgxMTYxOWJkNDBkYSIsInBpY3R1cmUiOiJodHRwczpcL1wvc21hcnQtc3RvcmFnZS1mMDYyOWYwMTc2MDU5LXN0YWdpbmcuczMuZXUtbm9ydGgtMS5hbWF6b25hd3MuY29tXC9wdWJsaWNcL1Byb2ZpbGVQaWN0dXJlc1wvRGVmYXVsdFByb2ZpbGVJbWFnZS5qcGVnIiwib3JpZ2luX2p0aSI6ImYzOGEwMjcwLTA1MGYtNGE4ZC1iYjBmLTZkZWQ2ZmQyZDY2NSIsImNvZ25pdG86cm9sZXMiOlsiYXJuOmF3czppYW06OjczMDMzNTM3NTkzOTpyb2xlXC9BZG1pblJvbGUiXSwiYXVkIjoiNnI2ZTcxc2V0NG1vZjJpM3R2a2d2NmluZW0iLCJldmVudF9pZCI6IjBiOTE3NDMyLTZjYTctNDY1MS05MDFiLWU1YWU1NTMzNmRkZSIsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNzIwOTgyNzk5LCJuYW1lIjoiVmljdG9yIiwicGhvbmVfbnVtYmVyIjoiKzI3NzM0NTQzODY5IiwiZXhwIjoxNzIwOTg2Mzk5LCJpYXQiOjE3MjA5ODI3OTksImZhbWlseV9uYW1lIjoiWmhvdSIsImp0aSI6Ijg5ODA3ODA2LTc3NjQtNGZkYy1hNjE4LTZlMzIxYjQ3Njg1YiIsImVtYWlsIjoiemhvdXZlbDdAZ21haWwuY29tIn0.YGxEtfYnr5pN9qGK3F1wPEdppdC_ZukzHj4BvEI6rogkbKN2d-EUnShHchd2jTh_YrnAf0cXoEZp_56QuLPL3gDAqjAnH6R2khBO6sr4Uolug1fM-kDINi0RySHIaIJYksKUf820h4qVUlUW4rHfUo041WBuqcdIm00OkD3xTlPx_I6H_7azs-KAdamtjCrnhtnuGJRH6Fq2wNX-kC8dTc9tJXVXRD8LMjWj76ayBo9wZCHTlFUDAwHMw_YhqV3vZicT6hgXiux6lFG0ITSrH3rR5wBOprmgJWNhkDsxiuq87jRPJwyKGoEQyuX5SAuCt6is1cX4Zubn667k-jtQVg'
    # Decode the token without verification (to extract the kid)
   # unverified_header = jwt.get_unverified_header(id_token)
    try:
    # Get the public key from Cognito
        cognito_region = 'us-east-1'
        user_pool_id = 'us-east-1_EPbgIUMEQ'
        keys_url = f'https://cognito-idp.{cognito_region}.amazonaws.com/{user_pool_id}/.well-known/jwks.json'
        response = requests.get(keys_url)
        keys = response.json()['keys']
        headers = jwt.get_unverified_header(id_token)
        kid = headers['kid']
        # Find the key by kid
        key = [k for k in keys if k['kid'] == kid][0]
        public_key = jwt.algorithms.RSAAlgorithm.from_jwk(json.dumps(key))
        # Verify the token
        #decoded = jwt.decode(id_token, public_key, algorithms=['RS256'], audience="6r6e71set4mof2i3tvkgv6inem")
        decoded_token= jwt.decode(id_token, options={"verify_signature": False})
        # Check if the user belongs to a specific group
        user_groups = decoded_token.get('cognito:groups', [])
        
        if 'Admin' in user_groups:
            # User is an admin
            pass
        else:
            # User is not an admin
            return {
                'statusCode': 403,
                'body': json.dumps(user_groups)
            }
        if 'cognito:groups' in decoded_token:
            groups = decoded_token['cognito:groups']
            print("Groups:", groups)
        else:
            print("This user does not belong to any groups.")
    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({"error": str(e)})
        }
event={}
context={}
print(lambda_handler(event, context))