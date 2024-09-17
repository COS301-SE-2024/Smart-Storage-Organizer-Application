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
    cur=con.cursor(cursor_factory=RealDictCursor)
    try:
        query='INSERT INTO changes_made (change_description,change_date,change_by,related_record_id,related_record_type,related_record_name,change_type,reason_for_change,organization_id,previous,current,comment) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
        cur.execute(query,(body['changes'],body['changes_date_and_time'],body['changes_by'],body['related_record_id'],body['related_record_type'],body['related_record_name'],body['changes_type'],body['Reason_for_change'],body['organization_id'],body['previous'],body['current']))
        con.commit()