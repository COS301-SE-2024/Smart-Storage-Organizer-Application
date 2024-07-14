import jwt
import json
import boto3
import requests
import json
import psycopg2
from psycopg2.extras import RealDictCursor

username="MasterUser"
password="MasterDb#ss1"
host_address="smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com"
DBname="postgres"
con = None
def get_db_connection():
    global con
    if con is None or con.closed:
        con = psycopg2.connect(
            host=host_address,
            database=DBname,
            user=username,
            password=password
         )
    return con
def search_unit(conn,curr,name):
    query="SELECT * FROM UNITS WHERE Unit_Name = %s"
    curr.execute(query, (name,))
    conn.commit()
    return curr.fetchall()

def add_constraints(constraint,unit_id,curr,conn):
    constraint_list=constraint.split(',')
    for i in constraint_list:
        query="INSERT INTO unit_category_constraint (category_id,unit_ID) VALUES (%s,%s)"
        parameters = (i,unit_id)
        curr.execute(query, parameters)
        conn.commit()
    return {
        'statusCode': 200,
        'body': 'Unit Added'
    }

def add_unit(conn,curr,event):
    try:
        if(search_unit(conn,curr,event['Unit_Name'])):
            return {
                'statusCode': 400,
                'body': 'Unit already exists'
            }
        else:
            query="INSERT INTO UNITS (Unit_Name,Unit_Capacity,Unit_QR,unit_capacity_used) VALUES (%s,%s,%s,%s)"
            parameters = (event['Unit_Name'], event['Unit_Capacity'], event['Unit_QR'],event['unit_capacity_used'])
            curr.execute(query, parameters)
            conn.commit()
            new_id=search_unit(conn,curr,event['Unit_Name'])[0]['unit_id']
            add_constraints(event['constraints'],new_id,curr,conn)
            return {
                'statusCode': 200,
                'body': 'Unit Added'
            }
    except Exception as e:
        conn.rollback()
        return {
            'statusCode': 500,
            'body': 'Error adding item',
            'error': json.dumps(str(e))
        }
def lambda_handler(event, context):
    # Assuming the ID token is passed in the event
    id_token = event['id_token']
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
        if 'guestUser' in user_groups:
            conn = get_db_connection()
            curr = conn.cursor(cursor_factory = RealDictCursor)
            return add_unit(conn,curr,event)
          
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
event={
    "Unit_Name": "Unit 1",
    "Unit_Capacity": 100,
    "Unit_QR": "https://www.qrstuff.com/",
    "unit_capacity_used": 0,
    "constraints": "1,2,3",
    "id_token":'eyJraWQiOiJDS2d3cFp2dGNVT0pVbjF2MGtxUEZ0VlgyQVFLM0N2NGtmMzRUS2R6endrPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEiLCJjb2duaXRvOmdyb3VwcyI6WyJndWVzdFVzZXIiXSwiZW1haWxfdmVyaWZpZWQiOnRydWUsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xX0VQYmdJVU1FUSIsInBob25lX251bWJlcl92ZXJpZmllZCI6ZmFsc2UsImNvZ25pdG86dXNlcm5hbWUiOiJiNGU4OTQyOC03MDUxLTcwNmUtODE3ZC04MTE2MTliZDQwZGEiLCJwaWN0dXJlIjoiaHR0cHM6XC9cL3NtYXJ0LXN0b3JhZ2UtZjA2MjlmMDE3NjA1OS1zdGFnaW5nLnMzLmV1LW5vcnRoLTEuYW1hem9uYXdzLmNvbVwvcHVibGljXC9Qcm9maWxlUGljdHVyZXNcL0RlZmF1bHRQcm9maWxlSW1hZ2UuanBlZyIsIm9yaWdpbl9qdGkiOiIzZDRiYjkxZS03MjU5LTQ4NzUtYWFmMS01MWRlNDg3MjM0OWYiLCJjb2duaXRvOnJvbGVzIjpbImFybjphd3M6aWFtOjo3MzAzMzUzNzU5Mzk6cm9sZVwvZ3Vlc3RSb2xlIl0sImF1ZCI6IjZyNmU3MXNldDRtb2YyaTN0dmtndjZpbmVtIiwiZXZlbnRfaWQiOiJjM2ExYjk3Zi04ZTNjLTRmYWEtYTFiMi04ZjBlZGFmZjllYjEiLCJ0b2tlbl91c2UiOiJpZCIsImF1dGhfdGltZSI6MTcyMDk4ODExNSwibmFtZSI6IlZpY3RvciIsInBob25lX251bWJlciI6IisyNzczNDU0Mzg2OSIsImV4cCI6MTcyMDk5MTcxNSwiaWF0IjoxNzIwOTg4MTE1LCJmYW1pbHlfbmFtZSI6Ilpob3UiLCJqdGkiOiI1MDM3Zjk3NC1lMmZmLTQ5ZWMtODVmOC1mYWRjMmUwMmZiOGEiLCJlbWFpbCI6Inpob3V2ZWw3QGdtYWlsLmNvbSJ9.BwES5OdXztfVpQmHVoNYPvIMN0r5L4m9_YZ7jZ57yM0lCCyWBDvr5iztdfnYcqcUTcrOmx25zm5Z_bbPKm07RLIzA1j1y2KcMhLJpql7vsIwlvvGAX2BLZlP32-pLGYO-tIozYhExfPtAyinFGjrXs4Er5Rm5BfXYV9DzDJo8pKpWFNDp5ITCRdnsi4WdnKsjhVAvIbqoUlk2U5ZMDJ5d__Ek7Qr1txizp1v5Ke8g67mWfcehkhjNSNV6knVMh6hn6yhM-Qy5b8fwYVWtFB98A_rHtsGbMJpcIU7vHfa68Q11Z1jmLeSBiOFBhcgHTDqGLSHvUVAVST549SduNhLFg'
}
context={}
print(lambda_handler(event, context))
