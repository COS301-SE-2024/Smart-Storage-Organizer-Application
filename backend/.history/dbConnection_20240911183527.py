import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os
import boto3


con = None
def get_db_connection():
    global con
    if con is None or con.closed:
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
    return con