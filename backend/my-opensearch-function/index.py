
import json
import os
import sys
sys.path.append('package')
import requests


try:
    password = os.environ.get('password')
    username = os.environ.get('username')
    region_name = os.environ.get('region' )
    host = os.environ.get('host')
    index = os.environ.get('index')
except KeyError as e:
    print("Environment variable not set: {}".format(e))
    sys.exit(1)

url = host + '/' + index + '/_search'

# Lambda execution starts here
def lambda_handler(event, context):
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
