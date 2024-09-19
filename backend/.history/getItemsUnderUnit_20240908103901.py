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
    list_of_lists = []
    for json_obj in body['subcategory']:
        list_of_lists.append(list(json_obj.values()))
    ids = [item[0] for item in list_of_lists]
    names = [item[1] for item in list_of_lists]

    print("IDs:", ids)
    print("Names:", names)
    subcategory_ids_placeholder = ', '.join(['%s'] * len(ids))
    query = f"""
    SELECT subcategoryid,
        COUNT(*) AS total_items
    FROM items
    WHERE subcategoryid IN ({subcategory_ids_placeholder})
    GROUP BY subcategoryid;
    """
    curr.execute(query, ids)
    conn.commit()
    results = curr.fetchall()
    print(results)
    if results:
        id_to_name = {item[0]: item[1] for item in list_of_lists}
        counts = {id_to_name[result['subcategoryid']]: result['total_items'] for result in results}
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

event={'body': {'organizationid': 1, 'subcategory': [{"id": 11, "categoryname": "Braai & Barbecue"}, {"id": 12, "categoryname": "All Patio"}]}}
print(lambda_handler(event, None))