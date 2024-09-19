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
event = {'body': {'organizationid': 1, 'subcategory': [{"id": 11, "categoryname": "Braai & Barbecue"}, {"id": 12, "categoryname": "All Patio"}, {"id": 13, "categoryname": "All Garden"}, {"id": 14, "categoryname": "All Pool"}, {"id": 15, "categoryname": "Others"}]', '[{"id": 16, "categoryname": "Vitamins & Supplements"}, {"id": 17, "categoryname": "First Aid"}, {"id": 18, "categoryname": "Medicine & Treatments"}, {"id": 19, "categoryname": "Sun Protection & Care"}, {"id": 20, "categoryname": "Others"}]', '[{"id": 21, "categoryname": "Kitchen"}, {"id": 22, "categoryname": "Dining & Entertaining"}, {"id": 23, "categoryname": "Furniture & Decor"}, {"id": 24, "categoryname": "Bedroom"}, {"id": 25, "categoryname": "Bathroom"}, {"id": 26, "categoryname": "Others"}]', '[{"id": 27, "categoryname": "Nappies & Changing"}, {"id": 28, "categoryname": "Maternity"}, {"id": 29, "categoryname": "Toys"}, {"id": 30, "categoryname": "Gear"}, {"id": 31, "categoryname": "Clothing"}, {"id": 32, "categoryname": "Nursery"}, {"id": 33, "categoryname": "Food"}, {"id": 34, "categoryname": "Health"}, {"id": 35, "categoryname": "Others"}]', '[{"id": 36, "categoryname": "All Automotive"}, {"id": 37, "categoryname": "All DIY Tools & Machinery"}, {"id": 38, "categoryname": "All Home Improvement"}, {"id": 39, "categoryname": "Others"}]', '[{"id": 40, "categoryname": "Men Grooming"}, {"id": 41, "categoryname": "Hair Care"}, {"id": 42, "categoryname": "Makeup"}, {"id": 43, "categoryname": "Woman Essentials"}, {"id": 44, "categoryname": "Others"}]', '[{"id": 45, "categoryname": "Educational"}, {"id": 46, "categoryname": "Fictional"}, {"id": 47, "categoryname": "Non Fictional"}, {"id": 48, "categoryname": "Others"}]', '[{"id": 49, "categoryname": "Jewellery"}, {"id": 50, "categoryname": "Footwear"}, {"id": 51, "categoryname": "Pants"}, {"id": 52, "categoryname": "Tops"}, {"id": 53, "categoryname": "Bags"}, {"id": 54, "categoryname": "Hats"}, {"id": 55, "categoryname": "Other"}]', '[{"id": 56, "categoryname": "TV, Audio & Media"}, {"id": 57, "categoryname": "Cellphones"}, {"id": 58, "categoryname": "Laptops"}, {"id": 59, "categoryname": "Wearable Tech"}, {"id": 60, "categoryname": "Computers"}, {"id": 61, "categoryname": "Monitor"}, {"id": 62, "categoryname": "Tablets"}, {"id": 63, "categoryname": "Camera"}, {"id": 64, "categoryname": "Others"}]', '[{"id": 65, "categoryname": "Running"}, {"id": 66, "categoryname": "Cycling"}, {"id": 67, "categoryname": "Weights"}, {"id": 68, "categoryname": "Soccer"}, {"id": 69, "categoryname": "Rugby"}, {"id": 70, "categoryname": "Tennis"}, {"id": 71, "categoryname": "Cricket"}]}}
print(lambda_handler(event, None))