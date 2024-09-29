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
def get_all_units(conn,curr,organizationid,parentcategory):

    query ="SELECT id, categoryname FROM category WHERE (organizationid =%s OR organizationid=0) AND parentcategory =%s"
    parameters=(organizationid,parentcategory)
    curr.execute(query,parameters)
    conn.commit()
    results = curr.fetchall()

    if results:

        return json.dumps(results)
    else:
        return {
            'statusCode': 404,
            'body': json.dumps({'error': 'No items found'})
        }


def getAllParentsIds(conn,curr,body):
    list_of_lists = [[item['id'], item['categoryname']] for item in body]
    ids = [item[0] for item in list_of_lists]
    names = [item[1] for item in list_of_lists]
    list=[]
    for i in ids:
        list.append(get_all_units(conn,curr,0,0))
    return list

def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
      response =getAllParentsIds(conn,curr,event['body'])
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
    return json.dumps(response)

event={'body': [{"id": 84, "categoryname": "Plants"}, {"id": 1, "categoryname": "Garden, Pool & Patio"}, {"id": 2, "categoryname": "Health & Personal Care"}]}
print(lambda_handler(event, None))

