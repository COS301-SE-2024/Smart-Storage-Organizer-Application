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
def getAllParentsCategories(conn,curr,body):

    query ="SELECT id, categoryname FROM category WHERE (organizationid =%s OR organizationid=0) AND parentcategory =%s"
    parameters=(body['organizationid'],'0')
    curr.execute(query,parameters)
    conn.commit()
    results = curr.fetchall()

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
    
def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        parentCategoriesResponse=getAllParentsCategories(conn,curr,event['body'])
         parentCategories = json.loads(parentCategoriesResponse['body'])
        list = [[item['id'], item['categoryname']] for item in parentCategoriesResponse['body']]
        print(list)
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