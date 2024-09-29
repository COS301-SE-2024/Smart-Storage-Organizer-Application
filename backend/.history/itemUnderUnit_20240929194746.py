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
def get_all_units(unit_name,organization_id,conn,curr):
    query="SELECT * FROM ITEMS where location=%s and organizationid=%s"
    curr.execute(query, (unit_name,organization_id))
    conn.commit()
    results= curr.fetchall()
    if results:
        if results!=None:
            units = [{'item_id': row['item_id'], 'item_name': row['item_name'] ,'description':row['description'],'colour_coding':row['colourcoding'],'qrcode':row ['qrcode'],'location':row['location'],'email':row['email'],"parentcategoryid":row['parentcategoryid'],"subcategoryid":row['subcategoryid'],"organizationid":row['organizationid'],"item_image":row['item_image'],"barcode":row['barcode'],"quanity":row['quanity']
                    } for row in results]
         
            return {
            'statusCode': 200,
            'body': json.dumps(units)
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
        unit_name=event['body']['unit_name']
        organization_id=event['body']['organization_id']
        response=get_all_units(unit_name,organization_id,conn,curr)
    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'Error getting item',
             'error': json.dumps(str(e))
         }
    finally:
       curr.close()
       conn.close()
    return response

