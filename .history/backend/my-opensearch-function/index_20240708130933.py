
import json

import sys
sys.path.append('package')
import requests


import boto3
from botocore.exceptions import ClientError


def get_secret():

    secret_name = "openSearchLogin"
    region_name = "eu-north-1"

    # Create a Secrets Manager client
    session = boto3.session.Session()
    client = session.client(
        service_name='secretsmanager',
        region_name=region_name
    )

    try:
        get_secret_value_response = client.get_secret_value(
            SecretId=secret_name
        )
    except ClientError :
        return 'failed'
    return  get_secret_value_response['SecretString']

    # Your code goes here.



region = 'eu-north-1' # For example, us-west-1
service = 'es'
host = 'https://search-sssearch-c2fixnkqyk2rux5ymz74atoebi.eu-north-1.es.amazonaws.com' # The OpenSearch domain endpoint with https:// and without a trailing slash
index = 'items'
url = host + '/' + index + '/_search'

# Lambda execution starts here
def lambda_handler(event, context):
    secrets = get_secret()
    if secrets == 'failed':
        return {
            'statusCode': 500,
            'body': json.dumps('Failed to get secrets')
        }
    else:
        username=secrets['openSearchUser']
        password=secrets['openSearchPassword']

    # Put the user query into the query DSL for more accurate search results.
    # Note that certain fields are boosted (^).
    query = {
        "size": 10,
        "query": {
            "multi_match": {
                "query": event['search'],
                "fields": ["item_name^4", "description^2", "colourcoding"],
                 "fuzziness": "AUTO"
            }
        }
    }

    # Elasticsearch 6.x requires an explicit Content-Type header
    headers = { "Content-Type": "application/json" }

    # Make the signed HTTP request
    r = requests.get(url, auth=(username,password), headers=headers, data=json.dumps(query))

    # Create the response and add some extra content to support CORS
    response = {
        "statusCode": 200,
        "headers": {
            "Access-Control-Allow-Origin": '*'
        },
        "isBase64Encoded": False
    }

    # Add the search results to the response
    response['body'] = r.text
    return response

event={
    'search':'balck'
}
context={}
print(lambda_handler(event, context))