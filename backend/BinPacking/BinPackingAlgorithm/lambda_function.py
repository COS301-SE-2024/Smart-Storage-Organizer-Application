from BinPacking import Packer, Bin, Item
import json
from decimal import Decimal

def decimal_to_float(obj):
    if isinstance(obj, Decimal):
        return float(obj)
    return obj


def parse_fitted_items(json_result):
    
    # Initialize a dictionary to store lists of fitted items for each bin
    parsed_bins = {}

    for bin_data in json_result['body']:
        bin_name = bin_data["bin"]
        parsed_bins[bin_name] = []

        for item in bin_data["fitted_items"]:
       
            # Create a tuple (XPosition, YPosition, ZPosition, Width, Height, Depth, Rotation)
            item_tuple = (
                item["Xposition"],  # XPosition
                item["Yposition"],  # YPosition
                item["Zposition"],  # ZPosition
                item["width"],        # Width
                item["height"],       # Height
                item["depth"],        # Depth
                item["rotation"]      # Rotation
            )

            # Add the tuple to the list for the current bin
            parsed_bins[bin_name].append(item_tuple)
    
    return parsed_bins

#event bin
    

def lambda_handler(event, context):
    result = []
    
    packer = Packer()
    for unit in event["Units"]:
        packer.addBin( Bin(partno=unit["ID"], WHD=(unit["width"],unit["height"],unit["depth"]),max_weight=unit["max_weight"], corner=0 ,put_type= 1 ))

    
    # width height depth
    for item in event["Items"]:
        packer.addItem(Item(partno=item["ID"], name=item["name"], typeof='cube', WHD=(item["width"],item["height"],item["depth"]), weight=item["weight"], level=1, loadbear=item["loadbear"], updown=item["updown"], color='#00F00'))



    packer.pack( bigger_first=True,               
        fix_point=True,                   
        binding=[],    
        distribute_items=False,            
        check_stable=True,            
        support_surface_ratio=0.50,  
        number_of_decimals=5)



    for b in packer.bins:
        bin_data = {
            "bin": b.partno,
            "fitted_items": [],
            "unfitted_items": [],
            "binWidth": decimal_to_float(b.width),
            "binHeight": decimal_to_float(b.height),
            "binDepth": decimal_to_float(b.depth)


        }

        # Collecting fitted items
        for item in b.items:
            fitted_item = {
                "id": item.partno,        # Assuming item.name gives the ID or name
                "width": decimal_to_float(item.width),
                "height": decimal_to_float(item.height),
                "depth": decimal_to_float(item.depth),
                "weight": decimal_to_float(item.weight),
                "rotation": item.rotation_type,
                "Xposition": decimal_to_float(item.position[0]),  # Assuming position is an attribute of the item
                "Yposition": decimal_to_float(item.position[1]),
                "Zposition": decimal_to_float(item.position[2])
            }
            bin_data["fitted_items"].append(fitted_item)

        # Collecting unfitted items
        for item in b.unfitted_items:
            unfitted_item = {
                "id": item.name,
                "width": decimal_to_float(item.width),
                "height": decimal_to_float(item.height),
                "depth": decimal_to_float(item.depth),
                "weight": decimal_to_float(item.weight)
            }
            bin_data["unfitted_items"].append(unfitted_item)

        # Append bin data to the result list
        result.append(bin_data)


    return {
            'statusCode': 200,
            'body': result,
            'message':"Successfully Recommended Arrangement"
        }



    # print (parse_fitted_items(json_result))


event ={
    "Units": [{"ID": "1", "width":100, "height":100, "depth":100, "max_weight": 100000}],
    "Items": [{"ID": "1","name":"sony" ,"width":20, "height":20, "depth":20, "weight": 1000, "loadbear": 50, "updown": 1}, {"ID": "2","name":"sony" ,"width":20, "height":20, "depth":20, "weight": 1000, "loadbear": 50, "updown": True}]
}


result =lambda_handler(event, "")

print(result)