import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os

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
def get_all_units(conn,curr,body):
    query="SELECT * FROM UNITS  WHERE organizationid =%s"

    curr.execute(query,(body['organization_id'],))
    conn.commit()
    results= curr.fetchall()
    if results:
        units = [{'id': row['unit_id'], 'name': row['unit_name'] ,'capacity':row['unit_capacity'],'capacity_used':row['unit_capacity_used']
                 } for row in results]
        return {
        'statusCode': 200,
        'body': json.dumps(units)
        }

def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        if 'body' not in event:
            return {
                'statusCode': 400,
                'body': 'Missing request body'
            }
        body=event['body']
        if isinstance(body, str):
            body = json.loads(body)

        response=get_all_units(conn,curr,body)
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'Error modifying item',
             'error': json.dumps(str(e))
         }
    finally:
       curr.close()
       conn.close()
    return response


body = {
    "body":{
        
    "organization_id": 1
    }
    }

print(lambda_handler(body, None))