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
def get_all_units(conn,curr,body):
    subcategories = body['subcategory']
    ids = [item['id'] for item in subcategories]
    names = [item['categoryname'] for item in subcategories]

    print("IDs:", ids)
    print("Names:", names)

    subcategory_ids_placeholder = ', '.join(['%s'] * len(ids))
    query = f"""
    SELECT
        subcategoryid,
        COUNT(*) AS total_items
    FROM items
    WHERE subcategoryid IN ({subcategory_ids_placeholder})
    GROUP BY subcategoryid;
    """
    curr.execute(query, ids)
    conn.commit()
    results = curr.fetchall()
    print(results)

    # Initialize counts with zeros
    id_to_name = {item['id']: item['categoryname'] for item in subcategories}
    counts = {name: 0 for name in names}

    # Update counts with actual results
    for result in results:
        counts[id_to_name[result['subcategoryid']]] = result['total_items']

    return {
        'statusCode': 200,
        'body': json.dumps(counts)
    }


def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        response=get_all_units(conn,curr,event['body'])
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

event={'body': {'organizationid': 1, 'subcategory': [{"id": 11, "categoryname": "Braai & Barbecue"}, {"id": 12, "categoryname": "All Patio"}, {"id": 13, "categoryname": "All Garden"}, {"id": 14, "categoryname": "All Pool"}, {"id": 15, "categoryname": "Others"}]}}
print(lambda_handler(event, None))