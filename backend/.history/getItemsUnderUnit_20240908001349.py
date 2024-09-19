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
            host=os.environ.get('Host_address'),
            database=os.environ.get('DB_Name'),
            user=os.environ.get('Username'),
            password=os.environ.get('Password')
         )
    return con
def get_all_units(conn,curr,body):
    subcategory_ids = body['subcategoryids']  # List of subcategory IDs
    subcategory_ids_placeholder = ', '.join(['%s'] * len(subcategory_ids))  # Create placeholders for the query

    query = f"""
    SELECT
        COUNT(*) AS total_units,
        {', '.join([f"COUNT(CASE WHEN subcategoryid = %s THEN 1 END) AS subcategory_{i+1}_count" for i in range(len(subcategory_ids))])}
    FROM items
    WHERE subcategoryid IN ({subcategory_ids_placeholder}) AND organizationid = %s;
    """
    params = (*subcategory_ids, *subcategory_ids, body['organizationid'])
    curr.execute(query, params)
    conn.commit()
    results = curr.fetchone()
    print (results)
    if results:
        counts = {
            'total_units': results['total_units'],
        }
        for i in range(len(subcategory_ids)):
            counts[f'subcategory_{i+1}_count'] = results[f'subcategory_{i+1}_count']
        return {
            'statusCode': 200,
            'body': json.dumps(counts)
        }
    else:
        return {
            'statusCode': 404,
            'body': json.dumps({'error': 'No items found'})
        }
def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        response=get_all_units(conn,curr,event['body'])
        print(response)
      #  response=get_all_units(conn,curr)
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