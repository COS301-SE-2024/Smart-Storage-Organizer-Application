import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os



con = None
def get_db_connection():
    global con
    if con is None or con.closed:
        try:
            con = psycopg2.connect(
            # host=os.environ.get('Host_address'),
            # database=os.environ.get('DB_Name'),
            # user=os.environ.get('Username'),
            # password=os.environ.get('Password')
            host="Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com",
            database="postgres",
            user="MasterUser",
            password="MasterDb#ss1"
           
            )
        except Exception as e:
            return{
                'statusCode': 500,
                'body': json.dumps({'error': 'Database connection error'})
            }
    return {
        'statusCode': 200,
        'body':con
    }
def lambda_handler(event, context):
    connection = get_db_connection()
    # Your code here
    return connection

print(lambda_handler({'body':{}},{}))