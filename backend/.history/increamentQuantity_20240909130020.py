import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os

con = None
opensearch_endpoint = 'https://your-opensearch-domain.com'
index_name = 'your-index-name'
document_id = 'your-document-id'
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
def increment_quantity(conn,curr,event):

    query = "UPDATE ITEMS SET quanity = %s WHERE item_id = %s"
    parameters = ( event['body']['quantity'],event['body']['item_id'],)
    curr.execute(query,parameters)
    conn.commit()
    return {
        'statusCode': 200,
        'body': json.dumps('Item updated successfully')
    }
def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        response=increment_quantity(conn,curr,event)
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

