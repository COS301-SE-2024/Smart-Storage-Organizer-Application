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
            host="Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com",
            database="postgres",
            user="MasterUser",
            password="MasterDb#ss1"

         )
    return con

def lambda_handler(event, context):
    con = get_db_connection()
    cur = con.cursor(cursor_factory=RealDictCursor)
    cur.execute("SELECT * FROM changes_made where organization_id=1")
    rows = cur.fetchall()
      for row in rows:
        for key, value in row.items():
    if isinstance(value, datetime):
        rows[key] = value.isoformat()
    return {
        "statusCode": 200,
        "body": json.dumps(rows)
    }
print(lambda_handler({},{}))