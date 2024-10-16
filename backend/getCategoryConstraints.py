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

def get_category_constraints(conn, curr, category_id):
    query = """
    SELECT u.unit_id, u.unit_name, u.unit_capacity, u.unit_qr, u.unit_capacity_used
    FROM units u
    INNER JOIN unit_category_constraint uc ON u.unit_id = uc.unit_id
    WHERE uc.category_id = %s;
    """

    curr.execute(query, (category_id,))
    results = curr.fetchall()
    if results:

        units = [{'id': row['unit_id'], 'name': row['unit_name'] ,'capacity':row['unit_capacity'],'capacity_used':row['unit_capacity_used']
                 } for row in results]
        return {
        'statusCode': 200,
        'body': json.dumps(units)
        }
    else:
        return {
            'statusCode': 404,
            'body': 'No units found for the given category'
        }


def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        response=get_category_constraints(conn,curr,event['category_id'])
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

event={
    "category_id": 1
}

context={}
print(lambda_handler(event, context))