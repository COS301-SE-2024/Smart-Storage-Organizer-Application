import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os

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
    SELECT
        COUNT(*) AS total_units,
        COUNT(CASE WHEN categoryid = 1 THEN 1 END) AS category1_count,
        COUNT(CASE WHEN categoryid = 2 THEN 1 END) AS category2_count
    FROM UNITS
    """
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
    response= lambda_client.invoke(
        FunctionName="",
        InvocationType='RequestResponse',
        Payload=json.dumps({
            "parentcategory": body['parentcategory'],
            "organizationid":body['organizationid']
        })
    )
    return  response['Payload'].read()

def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        response=get_all_units(conn,curr)
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