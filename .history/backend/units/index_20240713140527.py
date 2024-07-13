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
def search_unit(conn,curr,name):
    query="SELECT * FROM UNITS WHERE Unit_Name = %s"
    curr.execute(query, (name,))
    conn.commit()
    return curr.fetchall()
    
def delete_unit():
    print("delete unit")
def modify_unit():
   print('hello world')

def get_all_units(conn,curr):
    query="SELECT * FROM UNITS"
    curr.execute(query)
    conn.commit()
    return curr.fetchall()


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

def get_unit_constraints():
    print('hello world')

def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)
   
    response='a'
    try:
        if 'type'  in event:
        
            if event['type']=='AddUnit':
                return add(conn,curr,event)
                
            elif event['type']=='ModifyUnit':
                response= modify_unit()
            elif event['type']=='DeleteUnit':
                response=delete_unit()
            elif event['type']=='GetAllUnits':
                return get_all_units(conn,curr)
            elif event['type']=='GetUnitByName':
                response=search_unit(conn,curr,event['Unit_Name'])
                if(response):
                    return{
                        'statusCode': 200,
                        'body': json.dumps(response)
                    }
                else:
                    return{
                        'statusCode': 404,
                        'body': 'Unit not found'
                    }
            elif event['type']=='GetCategoryConstraints':
                return get_category_constraints(conn,curr,event['category_id'])
                
            else:
                return {
                    'statusCode': 400,
                    'body': 'Invalid type field in event object'
                }
            
        else :
            return {
                'statusCode': 400,
                'body': 'Missing type field in event object'
            }
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

event={"type":"AddUnit","Unit_Name":"Unit 5","Unit_Capacity":100,"Unit_QR":"QR1","constraints":"1,2,3,4", "unit_capacity_used":0}

context={}
print(lambda_handler(event, context))
