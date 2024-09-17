import json
import os
import sys

import psycopg2
from psycopg2.extras import RealDictCursor


con = None
def get_db_connection():
    global con
    if con is None or con.closed:
        con = psycopg2.connect(
            host=os.environ.get('Host_address'),
            database=os.environ.get('DB_Name'),
            user=os.environ.get('Username'),
            password=os.environ.get('Password')
            
         )
    return con

body={
    body:{
        "previous":{},
        "current":{},
        "changes":{},
        "changes_by":"",
        "changes_date_and_time":"",
        "related_record_id":{},
        "related_record_type":{},
        "related_record_name":{},
        "changes_type":{},
        "Reason_for_change":{},



    }
}

def lambda_handler(event,context):
    body=event['body']
    if isinstance(body, str):
        body = json.loads(body)

    con=get_db_connection()