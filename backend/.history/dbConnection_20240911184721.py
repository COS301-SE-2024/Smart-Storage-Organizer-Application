import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os



con = None
def get_db_connection():
    global con
    if con is None or con.closed:
        try:
            host=os.environ.get('Host_address'),
            database=os.environ.get('DB_Name'),
            user=os.environ.get('Username'),
            password=os.environ.get('Password')
           
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

