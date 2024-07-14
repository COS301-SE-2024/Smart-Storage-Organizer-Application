import jwt
import boto3
import requests
from jwt.algorithms import RSAAlgorithm
def lambda_handler(event, context):
    # Assuming the ID token is passed in the event
    id_token = 'eyJraWQiOiJDS2d3cFp2dGNVT0pVbjF2MGtxUEZ0VlgyQVFLM0N2NGtmMzRUS2R6endrPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfRVBiZ0lVTUVRIiwicGhvbmVfbnVtYmVyX3ZlcmlmaWVkIjpmYWxzZSwiY29nbml0bzp1c2VybmFtZSI6ImI0ZTg5NDI4LTcwNTEtNzA2ZS04MTdkLTgxMTYxOWJkNDBkYSIsInBpY3R1cmUiOiJodHRwczpcL1wvc21hcnQtc3RvcmFnZS1mMDYyOWYwMTc2MDU5LXN0YWdpbmcuczMuZXUtbm9ydGgtMS5hbWF6b25hd3MuY29tXC9wdWJsaWNcL1Byb2ZpbGVQaWN0dXJlc1wvRGVmYXVsdFByb2ZpbGVJbWFnZS5qcGVnIiwib3JpZ2luX2p0aSI6ImZiNmEzODJjLTA0ZmUtNGYzNC05MzhkLTA4OTg5MGI3NGJkOSIsImF1ZCI6IjZyNmU3MXNldDRtb2YyaTN0dmtndjZpbmVtIiwiZXZlbnRfaWQiOiJmZTA4NjM1OS1lN2U3LTQzNjYtYjEwYy1iOTY3Y2I1NTI1YWIiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTcyMDk3OTA2NywibmFtZSI6IlZpY3RvciIsInBob25lX251bWJlciI6IisyNzczNDU0Mzg2OSIsImV4cCI6MTcyMDk4MjY2NywiaWF0IjoxNzIwOTc5MDY3LCJmYW1pbHlfbmFtZSI6Ilpob3UiLCJqdGkiOiJmN2EwZjM4Ny1jODRjLTRjNWItOTFmNy02NmRiMjExZmMzN2MiLCJlbWFpbCI6Inpob3V2ZWw3QGdtYWlsLmNvbSJ9.RHKkScg-phjRuapgV5_p2P-wwX57xRat9XdJ7bNJM2KvFnkRtY9pzWUBl9G_LaDefqqaIx56kKAqWuLGTGm7H8erXjARoKxZwu2IQG3Iyo99xn_2QeTSHCIgqIGu8tTQ1MbnyC10EkbaFMefL1YKOHn8nZxZCfpoSe7vpLBZAQ2K0A3R0KtQWhS4j2B4wz1imSObjjJoGvcjMOLgWOuuBMtzrJWE7-xRtqUZ8pp0uPndTJF3Vx2gfH3aVnhavfiS03fSSo2BRrdZlvgG6DfubTPSK4cBrSZAFFLamzKZ_wBcNKgziTWS8OytiywZJBqwnZ1LCbm4zgAI6ENG4p-z7Q'
    # Decode the token without verification (to extract the kid)
    unverified_header = jwt.get_unverified_header(id_token)
    
    # Get the public key from Cognito
    cognito_region = 'us-east-1'
    user_pool_id = 'us-east-1_EPbgIUMEQ'
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
event={}
context={}
print(lambda_handler(event, context))