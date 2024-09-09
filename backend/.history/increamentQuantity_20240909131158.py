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
        opensearch_endpoint = 'https://your-opensearch-domain.com'
        index_name = 'your-index-name'
        document_id = 'your-document-id'

# Data to update
        update_data = {
            "doc": {
                "field_to_update": "new_value",
                "another_field_to_update": "another_new_value"
            }
        }

# Construct the URL for the update request
        url = f"{opensearch_endpoint}/{index_name}/_update/{document_id}"

# Send the update request
        response = requests.post(url, headers={"Content-Type": "application/json"}, data=json.dumps(update_data))

# Check the response
        if response.status_code == 200:
            print("Document updated successfully")
        else:
            print(f"Failed to update document: {response.status_code}")
        print(response.text)
        
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

