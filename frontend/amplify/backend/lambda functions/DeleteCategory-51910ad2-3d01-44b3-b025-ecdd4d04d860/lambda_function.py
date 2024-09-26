import json
import psycopg2
from psycopg2.extras import RealDictCursor
import os
import boto3

client = boto3.client('cognito-idp')
user_pool_id=os.getenv('UserPoolId')

username=os.getenv('DBUsername')
passcode=os.getenv('DBPassword')
hostadress=os.getenv('DBHost')
DBname=os.getenv('DBName')
con = None

def get_user_role(event):
    body = event['body']
    if isinstance(body, str):
        body = json.loads(body)
    username= body['username']
    try:
        response=client.admin_list_groups_for_user(
            UserPoolId=user_pool_id,
            Username=username
        )
        groups=response['Groups']
        for group in groups:
            if group['GroupName'] == 'Admin':
                group_name='Admin'
                break;
            elif group['GroupName'] == 'Manager':
                group_name="Manager"
                break;
            elif group['GroupName'] == 'guestUser':
                group_name="guestUser"
                break;
            elif group['GroupName'] == 'normalUser':
                group_name="normalUser"
                break;
        return group_name
    except client.exceptions.UserNotFoundException:
        return None 
    except client.exceptions.ClientError as error:
        return {
            "statusCode": 500,
            "body": 'Internal Server Error'
        }

def get_db_connection():
    global con
    if con is None or con.closed:
        
        con = psycopg2.connect(
            host=hostadress,
            database=DBname,
            user=username,
            password=passcode
            
         )
        
    return con

def lambda_handler(event, context):
    if 'body' not in event:
        event = {'body': event}
    user_role = get_user_role(event)
    if user_role == None:
        return {
            "statusCode": 403,
            "body": "You are not authorized to perform this action"
        }
        
    if user_role == 'guestUser':
        return {
            "statusCode": 403,
            "body": "You are not authorized to perform this action and need to be registered fully."
        }
    
    # TODO implement
    conn = get_db_connection()
    curr = conn.cursor(cursor_factory = RealDictCursor)

    
    try:  
        body = event['body']
        if isinstance(body, str):
            body = json.loads(body)       
        Query='Select id,useremail, parentcategory from category  where id=%s'
        
        parameters = (body['id'], )
        curr.execute(Query, parameters)
        record=curr.fetchall()
        
        if  record and record[0]['useremail'] =='NULL':
            #actions for delete default category
            Query='''
            insert into deletedcategory(organizationid, categoryid) values(%s, %s)
            '''
        
            parameters = (1,record[0]['id'])
        
            curr.execute(Query, parameters)

        elif record:
            #actions for deleting custom category
            Query='delete from category where id=%s'
            parameters = (body['id'], )
            curr.execute(Query, parameters)
            
        if record and record[0]['parentcategory']==0:
            #action for deleting subcategory
            if  record and record[0]['useremail'] =='NULL':
                #actions for deleting default subcategory
                Query = """
                INSERT INTO deletedcategory (organizationid, categoryid)
                SELECT organizationid, id
                FROM category
                WHERE parentcategory = %s;
                """
                parameters = (body['id'],)
                curr.execute(Query, parameters)
                


            elif record:
                #action for deleting custom category
                Query='delete from category where parentcategory=%s'
                parameters = (body['id'], )
                curr.execute(Query, parameters)

                

    except Exception as e:
      conn.rollback()
      return {
             'statusCode': 500,
             'body': 'unsuccessful attempt to remove category',
             'error': json.dumps(str(e))
         }
    finally:
        conn.commit()
        curr.close()
        conn.close()
       
    return {
            'statusCode': 200,
            'status': 'successfully remove category query' 
         }



event={
    
        'username':'Tshegofatso.Mapheto7@gmail.com',
        'id':86
    
}

print(lambda_handler(event, None))