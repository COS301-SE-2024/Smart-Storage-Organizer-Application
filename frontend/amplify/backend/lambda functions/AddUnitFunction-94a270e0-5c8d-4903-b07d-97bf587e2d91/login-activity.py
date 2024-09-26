import json
import os
import psycopg2
import psycopg2.extras
from datetime import datetime,timedelta



con = None

def get_db_connection():
    global con
    if con is None or con.closed:
        try:
            con = psycopg2.connect(
                # host=os.environ.get('Host_address'),
                # database=os.environ.get('DB_Name'),
                # user=os.environ.get('Username'),
                # password=os.environ.get('Password')

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

def lambda_handler(event, context):
    try:
        if event['body']:
            body = event['body']
            if isinstance(body, str):
                body = json.loads(body)
            required_fields = ['email', 'name', 'surname', 'time', 'type', 'organization_id']
            if not all(field in body for field in required_fields):
                return {
                    'statusCode': 400,
                    'body': 'Missing required fields'
                }
            if body['type'] == 'sign_in':
                response = sign_in(body)
                return response
            if body['type'] == 'sign_out':
                response = sign_out(body)
                return response
            if body['type'] == 'heartbeat_check':
                response = heartbeat_check(body)
                return response
    except Exception as e:
        return {
            'statusCode': 500,
            'body': 'Error processing request',
            'error': json.dumps(str(e))
        }

def sign_in(body):
    connection = get_db_connection()
    if isinstance(connection, dict) and connection.get('statusCode') == 500:
        return connection
    try:
        with connection.cursor() as cursor:
            date_str = body['time']
            date_obj = datetime.strptime(date_str, '%Y-%m-%d %H:%M:%S')
            timestamp = date_obj.strftime('%Y-%m-%d %H:%M:%S')
            
            queryCheck="Select * from login_activity where email = %s and organization_id = %s and time_out is null"
            cursor.execute(queryCheck, (body['email'], body['organization_id']))
            result = cursor.fetchone()
            if result:
                return {
                    'statusCode': 400,
                    'body': 'User is already logged in'
                }
            query = "INSERT INTO login_activity (email, name, surname, time_in, last_heartbeat,organization_id) VALUES (%s, %s, %s, %s,%s,%s)"
            cursor.execute(query, (body['email'], body['name'], body['surname'], timestamp,timestamp,body['organization_id']))
            connection.commit()

        return {
            'statusCode': 200,
            'body': 'Login activity recorded successfully'
        }
    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({'error': 'Query execution error', 'details': str(e)})
        }
    finally:
        if connection:
            connection.close()

def sign_out(body):

    connection = get_db_connection()
    if isinstance(connection, dict) and connection.get('statusCode') == 500:
        return connection
    try:
        with connection.cursor() as cursor:
            date_str = body['time']
            date_obj = datetime.strptime(date_str, '%Y-%m-%d %H:%M:%S')
            timestamp = date_obj.strftime('%Y-%m-%d %H:%M:%S')

            check_query = "SELECT * FROM login_activity WHERE email = %s AND organization_id = %s AND time_out IS NULL"
            cursor.execute(check_query, (body['email'], body['organization_id']))
            result = cursor.fetchone()

            if result:
                query = "UPDATE login_Activity set time_out = %s where email = %s and organization_id = %s and time_out is null "
                cursor.execute(query, ( timestamp,body['email'], body['organization_id']))
                connection.commit()

                return {
                    'statusCode': 200,
                    'body': 'Login activity recorded successfully'
                }
            else:
                return {
                    'statusCode': 404,
                    'body': 'User is not logged in'
                }
    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({'error': 'Query execution error', 'details': str(e)})
        }
    finally:
        if connection:
            connection.close()

def heartbeat(body):
    connection = get_db_connection()
    if isinstance(connection, dict) and connection.get('statusCode') == 500:
        return connection


    with connection.cursor() as cursor:
        try:
            query = "UPDATE login_activity SET last_heartbeat = %s WHERE email = %s AND time_out IS NULL and organization_id = %s"
            cursor.execute(query, (timestamp, body['email'], body['organization_id']))
            connection.commit()
            return{
                'statusCode': 200,
                'body': 'Heartbeat recorded successfully'
            }
        except Exception as e:
            return {
                'statusCode': 500,
                'body': json.dumps({'error': 'Query execution error', 'details': str(e)})
                }
        finally:
            if connection:
                connection.close()

def heartbeat_check(body):
    connection = get_db_connection()
    if isinstance(connection, dict) and connection.get('statusCode') == 500:
        return connection
    try:

        with connection.cursor() as cursor:
            # Check the last heartbeat
            check_query = "SELECT last_heartbeat FROM login_activity WHERE email = %s AND organization_id = %s AND time_out IS NULL"
            cursor.execute(check_query, (body['email'], body['organization_id']))
            result = cursor.fetchone()

            if result and result[0]:
                last_heartbeat = result[0]
                current_time = datetime.now()

                current_time = datetime.now()

                # Ensure last_heartbeat is a datetime object
                if isinstance(last_heartbeat, str):
                    last_heartbeat = datetime.strptime(last_heartbeat, '%Y-%m-%d %H:%M:%S')
                if current_time - last_heartbeat > timedelta(minutes=10):
                    return {
                        'statusCode': 400,
                        'body': 'Heartbeat is more than 10 minutes ago'
                    }

            # Update the last heartbeat
            update_query = "UPDATE login_activity SET last_heartbeat = %s WHERE email = %s AND organization_id = %s AND time_out IS NULL"
            cursor.execute(update_query, (timestamp, body['email'], body['organization_id']))
            connection.commit()
        return {
            'statusCode': 200,
            'body': 'Heartbeat recorded successfully'
        }
    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({'error': 'Query execution error', 'details': str(e)})
        }
    finally:
        if connection:
            connection.close()


# event ={
#     "body": json.dumps({
#         "email": "admin",
#         "name": "victor",
#         "surname": "zhou",
#         "type": "sign_in",
#         "organization_id": 1,
#         "time": "2024-09-12 11:52:16"
#     })

# }
# print(lambda_handler(event , {}))