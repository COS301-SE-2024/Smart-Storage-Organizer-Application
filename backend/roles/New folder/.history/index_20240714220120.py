import jwt
import json
import boto3
import requests

def lambda_handler(event, context):
    # Assuming the ID token is passed in the event
    id_token = 'eyJraWQiOiJDS2d3cFp2dGNVT0pVbjF2MGtxUEZ0VlgyQVFLM0N2NGtmMzRUS2R6endrPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEiLCJjb2duaXRvOmdyb3VwcyI6WyJub3JtYWxVc2VyIl0sImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV9FUGJnSVVNRVEiLCJwaG9uZV9udW1iZXJfdmVyaWZpZWQiOmZhbHNlLCJjb2duaXRvOnVzZXJuYW1lIjoiYjRlODk0MjgtNzA1MS03MDZlLTgxN2QtODExNjE5YmQ0MGRhIiwicGljdHVyZSI6Imh0dHBzOlwvXC9zbWFydC1zdG9yYWdlLWYwNjI5ZjAxNzYwNTktc3RhZ2luZy5zMy5ldS1ub3J0aC0xLmFtYXpvbmF3cy5jb21cL3B1YmxpY1wvUHJvZmlsZVBpY3R1cmVzXC9EZWZhdWx0UHJvZmlsZUltYWdlLmpwZWciLCJvcmlnaW5fanRpIjoiNDg1ZDY2MTYtNDQyNi00MGVhLTg2N2EtOTg3MjdhYTIwNDQ4IiwiY29nbml0bzpyb2xlcyI6WyJhcm46YXdzOmlhbTo6NzMwMzM1Mzc1OTM5OnJvbGVcL25vcm1hbFJvbGUiXSwiYXVkIjoiNnI2ZTcxc2V0NG1vZjJpM3R2a2d2NmluZW0iLCJldmVudF9pZCI6IjhkZDY0N2E2LTY2NzctNDIzMy04NmZjLTlkYmJiNGRlYjBhMCIsInRva2VuX3VzZSI6ImlkIiwiYXV0aF90aW1lIjoxNzIwOTg3MjU3LCJuYW1lIjoiVmljdG9yIiwicGhvbmVfbnVtYmVyIjoiKzI3NzM0NTQzODY5IiwiZXhwIjoxNzIwOTkwODU3LCJpYXQiOjE3MjA5ODcyNTcsImZhbWlseV9uYW1lIjoiWmhvdSIsImp0aSI6IjQyZTFjNDI1LTRjNzAtNDAxZC04M2M2LTA0MmM3MGE4ZDQ3ZSIsImVtYWlsIjoiemhvdXZlbDdAZ21haWwuY29tIn0.N5d--ES0EfvfADN8T2Xf7zOTXi9M2Z98h-awVhdms-E96dsr6wyFOTONz91sgKkTQvCEkxZTW-xXltduMAObZquZT9MSYuxY4aNzHonlgd1PTmNoJdp3k0oRxFHI3JihERsdymUeGrDH4206JueOgo9B2MDmpYFlay-bxdMrxrfKTiM3V_OpxRhujNlsgIv5Mv3NR-6lyP5pnONH-GeXCieot0ELEH8HFjV2WSSTDQ2PYwXyP1xx_oV88p8tiIWUmbqhJXAWh71dVykx6jQieLRdSp85FC_V8ag5WA8gSudhj2GNea_MrxcnUV7kXw4CMMfkKsw-XcENjyR13j5sxQ'
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
           return{
                'statusCode': 200,
                'body': json.dumps(user_groups)
           }
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
