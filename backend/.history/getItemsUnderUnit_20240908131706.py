import json
import psycopg2
from psycopg2.extras import RealDictCursor

def get_db_connection():
    con = psycopg2.connect(
        # host="localhost",
        # database="your_database",
        # user="your_user",
        # password="your_password"

        host="Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com",
        database="postgres",
        user="MasterUser",
        password="MasterDb#ss1"
    )
    return con

def get_all_units(conn, curr, body):
    subcategories = body['subcategory']
    ids = [item['id'] for item in subcategories]
    names = [item['categoryname'] for item in subcategories]

    print("IDs:", ids)
    print("Names:", names)

    subcategory_ids_placeholder = ', '.join(['%s'] * len(ids))
    query = f"""
    SELECT
        subcategoryid,
        COUNT(*) AS total_items
    FROM items
    WHERE subcategoryid IN ({subcategory_ids_placeholder})
    GROUP BY subcategoryid;
    """
    curr.execute(query, ids)
    conn.commit()
    results = curr.fetchall()
    print(results)

    # Initialize counts with zeros
    id_to_name = {item['id']: item['categoryname'] for item in subcategories}
    counts = {name: 0 for name in names}

    # Update counts with actual results
    for result in results:
        if result['subcategoryid'] in id_to_name:
            counts[id_to_name[result['subcategoryid']]] = result['total_items']
        else:
            print(f"Warning: subcategoryid {result['subcategoryid']} not found in provided IDs")

    return {
        'statusCode': 200,
        'body': json.dumps(counts)
    }

def lambda_handler(event, context):
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory=RealDictCursor)

    try:
        response = get_all_units(conn, curr, event['body'])
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

# Example event
event = {'body': {'organizationid': 1, 'subcategory': ["[{\"id\": 1, \"categoryname\": \"Garden, Pool & Patio\"}, {\"id\": 2, \"categoryname\": \"Health & Personal Care\"}, {\"id\": 3, \"categoryname\": \"Home & Appliances\"}, {\"id\": 4, \"categoryname\": \"Baby & Toddler\"}, {\"id\": 5, \"categoryname\": \"Automative & DIY\"}, {\"id\": 6, \"categoryname\": \"Beauty\"}, {\"id\": 7, \"categoryname\": \"Books\"}, {\"id\": 8, \"categoryname\": \"Clothing, Shoes, Accessories\"}, {\"id\": 9, \"categoryname\": \"Electronics\"}, {\"id\": 10, \"categoryname\": \"Sport & Training\"}]", "[{\"id\": 1, \"categoryname\": \"Garden, Pool & Patio\"}, {\"id\": 2, \"categoryname\": \"Health & Personal Care\"}, {\"id\": 3, \"categoryname\": \"Home & Appliances\"}, {\"id\": 4, \"categoryname\": \"Baby & Toddler\"}, {\"id\": 5, \"categoryname\": \"Automative & DIY\"}, {\"id\": 6, \"categoryname\": \"Beauty\"}, {\"id\": 7, \"categoryname\": \"Books\"}, {\"id\": 8, \"categoryname\": \"Clothing, Shoes, Accessories\"}, {\"id\": 9, \"categoryname\": \"Electronics\"}, {\"id\": 10, \"categoryname\": \"Sport & Training\"}]", "[{\"id\": 1, \"categoryname\": \"Garden, Pool & Patio\"}, {\"id\": 2, \"categoryname\": \"Health & Personal Care\"}, {\"id\": 3, \"categoryname\": \"Home & Appliances\"}, {\"id\": 4, \"categoryname\": \"Baby & Toddler\"}, {\"id\": 5, \"categoryname\": \"Automative & DIY\"}, {\"id\": 6, \"categoryname\": \"Beauty\"}, {\"id\": 7, \"categoryname\": \"Books\"}, {\"id\": 8, \"categoryname\": \"Clothing, Shoes, Accessories\"}, {\"id\": 9, \"categoryname\": \"Electronics\"}, {\"id\": 10, \"categoryname\": \"Sport & Training\"}]"]}}
print(lambda_handler(event, None))

