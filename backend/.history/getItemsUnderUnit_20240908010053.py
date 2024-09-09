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
    print("ids",ids)
    names = [item[1] for item in list_of_lists]
    subcategory_ids_placeholder = ', '.join(['%s'] * len(ids))  # Create placeholders for the query
  
    query = f"""
    SELECT
        COUNT(*) AS total_units,
        {', '.join([f"COUNT(CASE WHEN subcategoryid = %s THEN 1 END) AS subcategory_{i+1}_count" for i in range(len(ids))])}
    FROM items
    WHERE subcategoryid IN ({ids}) AND organizationid = %s;
    """
    params = (*ids, *ids, body['organizationid'])
    curr.execute(query, params)
    conn.commit()
    results = curr.fetchone()
    print (results)
    if results:
        counts = {
            'total_units': results['total_units'],
        }
        for i in range(len(ids)):
            counts[names[i]] = results[{i+1}]
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

event={'body': {'organizationid': 1, 'subcategory': [{'id': 1, 'name': 'subcategory1'}, {'id': 2, 'name': 'subcategory2'}]}}
print(lambda_handler(event, None))