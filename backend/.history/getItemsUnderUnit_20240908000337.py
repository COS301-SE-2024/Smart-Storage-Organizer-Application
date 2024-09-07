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
def get_all_units(conn,curr):
    query = """
    SELECT COUNT(*) AS item_count
    FROM items
    WHERE subcategoryid=%s AND organizationid=%s;
    """
    params = (body['parentcategory'], body['organizationid'])
    curr.execute(query)
    conn.commit()
    results = curr.fetchone()
    if results:
        counts = {
            'total_units': results['total_units'],
            'category1_count': results['category1_count'],
            'category2_count': results['category2_count']
        }
        return {
            'statusCode': 200,
            'body': json.dumps(counts)
        }
def parentIds(body):
    lambda_client = boto3.client('lambda')
    response= lambda_client.invoke(
        FunctionName="CategoryFilter",
        InvocationType='RequestResponse',
        Payload=json.dumps({
            "parentcategory": body['parentcategory'],
            "organizationid":body['organizationid'],
            "limit":body['limit'],
            "offset":body['offset']
        })
    )
    return  response['Payload'].read()

def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        response=parentIds(event['body'])
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