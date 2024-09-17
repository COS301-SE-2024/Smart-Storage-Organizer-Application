import json
import os
import sys

import psycopg2
from psycopg2.extras import RealDictCursor


con = None
def get_db_connection():
    global con
    if con is None or con.closed:
        con = psycopg2.connect(
            host="Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com",
            database="postgres",
            user="MasterUser",
            password="MasterDb#ss1"
            
         )
    return con

body={
    "body":{
        "previous":{
            "item_id":29,
            "item_name":"IPHONE 15",
            "item_description":"IPHONE 15 256GB",
            "item_price":1200.0,
            "item_quantity":10,
            "item_category":"ELECTRONICS",
            "item_subcategory":"MOBILE",
            "item_brand":"APPLE",
            "item_model":"IPHONE 15",
            "item_color":"BLACK",
            "item_size":"6.1 INCH",
            "item_weight":"200G",
            "item_material":"ALUMINIUM"
        },
        "current":{"item_id":29,"item_name":"IPHONE 15","item_description":"IPHONE 15 256GB","item_price":1200.0,"item_quantity":10,"item_category":"ELECTRONICS","item_subcategory":"MOBILE","item_brand":"APPLE","item_model":"IPHONE 15","item_color":"BLACK","item_size":"6.1 INCH","item_weight":"200G","item_material":"ALUMINIUM"},
        "changes":"",
        "change_by":"",
        "changes_date_and_time":"2024-09-12 11:52:16",
        "related_record_id":29,
        "related_record_type":"ITEM",
        "related_record_name":"IPHONE 15",
        "changes_type":"EDIT",
        "Reason_for_change":"WRONG SPELLING IN ITEM DESCRIPTION",
        "organization_id":1



    }
}

def lambda_handler(event,context):
    body=event['body']
    if isinstance(body, str):
        body = json.loads(body)

    if 'changes' not in body or 'changes_date_and_time' not in body or 'changes_by' not in body or 'related_record_id' not in body or 'related_record_type' not in body or 'related_record_name' not in body or 'changes_type' not in body or 'Reason_for_change' not in body or 'organization_id' not in body or 'previous' not in body or 'current' not in body:
        return {
            'statusCode': 400,
            'body': json.dumps('Invalid Request')
        }
  
    con=get_db_connection()
    cur=con.cursor(cursor_factory=RealDictCursor)
    try:
        query='INSERT INTO changes_made (change_description,change_date,change_by,related_record_id,related_record_type,related_record_name,change_type,reason_for_change,organization_id,previous,current,comment) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)'
        cur.execute(query,(body['changes'],body['changes_date_and_time'],body['changes_by'],body['related_record_id'],body['related_record_type'],body['related_record_name'],body['changes_type'],body['Reason_for_change'],body['organization_id'], json.dumps(body['previous']), json.dumps(body['current'])))
        con.commit()
    except Exception as e:
        print(e)
        return {
            'statusCode': 500,
            'body': json.dumps('Internal Server Error')
        }
    finally:
        con.close()
        return {
            'statusCode': 200,
            'body': json.dumps('Changes saved successfully')
        }


print(lambda_handler(body,{}))