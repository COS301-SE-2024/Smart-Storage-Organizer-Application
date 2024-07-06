import boto3
import json

import sys
sys.path.append('package')
import requests
from requests_aws4auth import AWS4Auth
masterUser1 = 'MasterUser1'
masterUser= 'Password123#'

region = 'eu-north-1' # For example, us-west-1
service = 'es'
#credentials = boto3.Session().get_credentials()
#awsauth = AWS4Auth(credentials.access_key, credentials.secret_key, region, service, session_token=credentials.token)

host = 'https://search-sssearch-c2fixnkqyk2rux5ymz74atoebi.aos.eu-north-1.on.aws/_dashboards' # The OpenSearch domain endpoint with https:// and without a trailing slash
index = 'movies'
url = host + '/' + index + '/_search'

# Lambda execution starts here
def lambda_handler(event, context):

    # Put the user query into the query DSL for more accurate search results.
    # Note that certain fields are boosted (^).
    query = {
        "size": 25,
        "query": {
            "multi_match": {
                "query": event['q'],
                "fields": ["title^4", "plot^2", "actors", "directors"]
            }
        }
    }

    # Elasticsearch 6.x requires an explicit Content-Type header
    headers = { "Content-Type": "application/json" }

    # Make the signed HTTP request
    r = requests.get(url, auth=(masterUser1,masterUser), headers=headers, data=json.dumps(query))

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
    "q": "mars"
}
context={}
print(lambda_handler(event, context))