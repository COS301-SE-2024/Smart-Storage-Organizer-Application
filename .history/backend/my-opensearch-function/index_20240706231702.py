
import json

import sys
sys.path.append('package')
import requests

masterUser1 = 'MasterUser1'
masterUser= 'Password123#'

region = 'eu-north-1' # For example, us-west-1
service = 'es'
host = 'https://search-sssearch-c2fixnkqyk2rux5ymz74atoebi.eu-north-1.es.amazonaws.com' # The OpenSearch domain endpoint with https:// and without a trailing slash
index = 'items'
url = host + '/' + index + '/_search'

# Lambda execution starts here
def lambda_handler(event, context):

    # Put the user query into the query DSL for more accurate search results.
    # Note that certain fields are boosted (^).
    query = {
        "size": 25,
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
    'q':'balck'
}
context={}
print(lambda_handler(event, context))  