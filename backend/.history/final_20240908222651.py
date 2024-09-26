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
    
def getSubCategories(conn,curr,organizationid,parentcategoryIds):
    query = "SELECT id, categoryname FROM category WHERE parentcategory= %s AND (organizationid = %s  OR organizationid = 0)"
    # Execute the query
    parameters=(parentcategoryIds,organizationid)
    curr.execute(query,parameters)
    # Fetch the results
    results = curr.fetchall()
    return json.dumps(results)

def getSubCategoriesTotals(conn, curr,organizationid, ids,names,parentCategoryName):
   
    subcategory_ids_placeholder = ', '.join(['%s'] * len(ids))
   
    query = f"""
    SELECT
        subcategoryid,
        SUM(quantity) AS total_quantity,
        COUNT(*) AS total_items
    FROM items
    WHERE subcategoryid IN ({subcategory_ids_placeholder}) AND organizationid ={'%s'}
    GROUP BY subcategoryid;
    """
    params = ids + [organizationid]
    curr.execute(query, params)
    conn.commit()
    results = curr.fetchall()
  

    # Initialize counts with zeros
    id_to_name = dict(zip(ids, names))
    counts = {name: 0 for name in names}
    totalAMount=0
    totalUnique=0
    # Update counts with actual results
    for result in results:
        if result['subcategoryid'] in id_to_name:
            counts[id_to_name[result['subcategoryid']]] = result['total_items']
            counts[id_to_name[result['subcategoryid']]]['total_items'] = result['total_items']
            counts[id_to_name[result['subcategoryid']]]['total_quantity'] = result['total_quantity']
            totalAMount += result['total_items']
        else:
            print(f"Warning: subcategoryid {result['subcategoryid']} not found in provided IDs")

    return {
        "Category":parentCategoryName,
       "Subcategories":  json.dumps(counts),
        "totalAMount": totalAMount,
        "totalUniqueItems": totalUnique
    }


def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    try:
        organizationid = event['body']['organizationid']
        parentCategoriesResponse=getAllParentsCategories(conn,curr,event['body'])
        parentCategories = json.loads(parentCategoriesResponse['body'])
        ids = [item['id'] for item in parentCategories]
        names = [item['categoryname'] for item in parentCategories]
        
        finalList=[]
        for i,j in zip(ids, names):
            
            subCategories=getSubCategories(conn,curr,organizationid,i)
            
            if  subCategories=="[]":
                finalList.append({"Category":j,"Subcategories":"No subcategories", "totalAMount":0})
                continue
            subsId=[item['id'] for item in json.loads(subCategories)]
            subsName=[item['categoryname'] for item in json.loads(subCategories)]
            finalList.append(getSubCategoriesTotals(conn, curr,organizationid,subsId,subsName,j))

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
        'body': json.dumps(finalList)
    }

event={'body': {'organizationid': 1, 'parentcategory': 9}}
print(lambda_handler(event, None))