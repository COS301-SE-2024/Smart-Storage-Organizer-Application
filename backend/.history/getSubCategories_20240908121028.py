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
def get_all_units(conn,curr,organizationid,parentcategory,body):

    query ="SELECT id, categoryname FROM category WHERE (organizationid =%s OR organizationid=0) AND parentcategory =%s"
    parameters=(organizationid,parentcategory)
    curr.execute(query,parameters)
    conn.commit()
    results = curr.fetchall()
    print(results)
    if results:
        
        return {
        'statusCode': 200,
        'body': json.dumps(results)
        }
    else:
        return {
            'statusCode': 404,
            'body': json.dumps({'error': 'No items found'})
        }
    

def getAllParentsIds(conn,curr,body):
    list_of_lists = [[item['id'], item['categoryname']] for item in body]
    ids = [item[0] for item in list_of_lists]
    names = [item[1] for item in list_of_lists]
    for i in ids:
        responce=get_all_units(conn,curr,body['organizationid'],body['parentcategory'],body)

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

event={'body': {'organizationid': 1, 'parentcategory': 9}}
print(lambda_handler(event, None))