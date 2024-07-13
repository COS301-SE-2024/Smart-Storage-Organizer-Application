import json
import psycopg2
from psycopg2.extras import RealDictCursor

username="MasterUser"
password="MasterDb#ss1"
host_address="smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com"
DBname="postgres"
con = None
def get_db_connection():
    global con
    if con is None or con.closed:
        con = psycopg2.connect(
            host=host_address,
            database=DBname,
            user=username,
            password=password
         )
    return con
def search_unit(conn,curr,name):
    query="SELECT * FROM UNITS WHERE Unit_Name = %s"
    curr.execute(query, (name,))
    conn.commit()
    return curr.fetchall()

def add_constraints(constraint,unit_id,curr,conn):
    constraint_list=constraint.split(',')
    for i in constraint_list:
        query="INSERT INTO unit_category_constraint (category_id,unit_ID) VALUES (%s,%s)"
        parameters = (i,unit_id)
        curr.execute(query, parameters)
        conn.commit()
    return {
        'statusCode': 200,
        'body': 'Unit Added'
    }

def add_unit(conn,curr,event):
    try:
        if(search_unit(conn,curr,event['Unit_Name'])):
            return {
                'statusCode': 400,
                'body': 'Unit already exists'
            }
        else:
            query="INSERT INTO UNITS (Unit_Name,Unit_Capacity,Unit_QR,unit_capacity_used) VALUES (%s,%s,%s,%s)"
            parameters = (event['Unit_Name'], event['Unit_Capacity'], event['Unit_QR'],event['unit_capacity_used'])
            curr.execute(query, parameters)
            conn.commit()
            new_id=search_unit(conn,curr,event['Unit_Name'])[0]['unit_id']
            add_constraints(event['constraints'],new_id,curr,conn)
            return {
                'statusCode': 200,
                'body': 'Unit Added'
            }
    except Exception as e:
        conn.rollback()
        return {
            'statusCode': 500,
            'body': 'Error adding item',
            'error': json.dumps(str(e))
        }
    
def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        if 'type'  in event:
                return add_unit(conn,curr,event)
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
    return {
            'statusCode': 200,
            'body': 'Operation Successful'
        }

# event={"Unit_Name":"Unit 5","Unit_Capacity":100,"Unit_QR":"QR1","constraints":"1,2,3,4", "unit_capacity_used":0}

# context={}
# print(lambda_handler(event, context))