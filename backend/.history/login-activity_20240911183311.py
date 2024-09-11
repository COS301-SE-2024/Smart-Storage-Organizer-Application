import json
import os
import sys

body={
    "email": "admin",
    "name":"victor",
    "surname":"zhou",
    "time":"2021-05-06",
    "type":"login",
    "organization":1
}



def login(conn,curr, body):
    query="INSERT INTO login_activity (email, name, surname, time_in ,organization_id) VALUES (%s, %s, %s, %s, %s)"
    parameters=(body['email'],body['name'],body['surname'],body['time'],body['organization'])

