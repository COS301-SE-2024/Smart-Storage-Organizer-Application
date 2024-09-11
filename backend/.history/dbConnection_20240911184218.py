import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os
import boto3


con = None
def get_db_connection():
    global con
    if con is None or con.closed:
        try:
            con = psycopg2.connect(
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
        'body':json.dumps(con)
    }

print(get_db_connection())
