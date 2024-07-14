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
  
}
context={}
print(lambda_handler(event, context))
