import os
import json
import psycopg2
import psycopg2.extras
from datetime import datetime,timedelta


con = None

def get_db_connection():
    global con
    if con is None or con.closed:
        try:
            con = psycopg2.connect(
                host="Smartstoragedb.c7ymg4sywvej.eu-north-1.rds.amazonaws.com",
                database="postgres",
                user="MasterUser",
                password="MasterDb#ss1"
            )
        except Exception as e:
            return {
                'statusCode': 500,
                'body': json.dumps({'error': 'Database connection error', 'details': str(e)})
            }
    return con

def login_reports(body):
    connection = get_db_connection()
    if isinstance(connection, dict) and connection.get('statusCode') == 500:
        return connection
    try:
        with connection.cursor() as cursor:
            query = "SELECT id,email,name,surname,time_in,time_out FROM login_activity WHERE organization_id = %s"
            parameters = (body['organization_id'],)
            cursor.execute(query, parameters)
            connection.commit()
            results = cursor.fetchall()
            if results:
               login_activity = [{'email': row[0], 'name': row[1], 'surname': row[2], 'time': row[3], 'type': row[4], 'organization_id': row[5]} for row in results]
                return {
                    'statusCode': 200,
                    'body': json.dumps(login_activity)
                }
            else:
                return {
                    'statusCode': 404,
                    'body': json.dumps({'error': 'No items found'})
                }
    except Exception as e:
        connection.rollback()
        return {
            'statusCode': 500,
            'body': 'Error modifying item',
            'error': json.dumps(str(e))
        }
    finally:
        cursor.close()
        connection.close()

def lambda_handler(event,context):
    try:
        if event['body']:
            body = event['body']
            if isinstance(body, str):
                body = json.loads(body)
            if 'organization_id' not in body:
                return {
                    'statusCode': 400,
                    'body': 'Missing required fields'
                }
            return login_reports(body)

    except Exception as e:
        return {
            'statusCode': 500,
            'body': 'Error processing request',
            'error': json.dumps(str(e))
        }

print(lambda_handler({'body': {'organization_id': 1}}, None))