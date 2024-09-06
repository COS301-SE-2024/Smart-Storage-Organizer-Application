import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier


container_data = {
    'container_id': [1, 2, 3],  # Different containers
    'width': [15, 10, 12],      # Width of the containers
    'height': [15, 12, 10],     # Height of the containers
    'depth': [10, 10, 12],      # Depth of the containers
    'available_width': [15, 10, 12],  # Current available space
    'available_height': [15, 12, 10], # Current available space
    'available_depth': [10, 10, 12],  # Current available space
}


item_data = {
    'item_id': [101, 102, 103, 104],
    'width': [5, 8, 7, 6],
    'height': [5, 7, 6, 6],
    'depth': [5, 7, 5, 6],
    'container_id': [1, 2, 3, 1],
}

df_containers = pd.DataFrame(container_data)
df_items = pd.DataFrame(item_data)

# Decision Tree Model to assign items to containers based on their dimensions
X = df_items[['width', 'height', 'depth']]
y = df_items['container_id']  # Use manually assigned container IDs as labels

# Split the data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Create and train the Decision Tree model
model = DecisionTreeClassifier()
model.fit(X_train, y_train)

# Function to check if an item fits in a container
def check_if_item_fits(item, container):
    return (
        item['width'] <= container['available_width'] and
        item['height'] <= container['available_height'] and
        item['depth'] <= container['available_depth']
    )

# Function to place item in a container and update available space
def place_item_in_container(item, container):
    df_containers.loc[df_containers['container_id'] == container['container_id'], 'available_width'] -= item['width']
    df_containers.loc[df_containers['container_id'] == container['container_id'], 'available_height'] -= item['height']
    df_containers.loc[df_containers['container_id'] == container['container_id'], 'available_depth'] -= item['depth']

# Function to create a new container when all existing containers are full
def create_new_container():
    new_container_id = df_containers['container_id'].max() + 1
    new_container = {
        'container_id': new_container_id,
        'width': 15,               # Define the dimensions for new containers
        'height': 15,
        'depth': 10,
        'available_width': 15,
        'available_height': 15,
        'available_depth': 10
    }
    df_containers.loc[len(df_containers)] = new_container
    print(f"New container {new_container_id} created.")
    return new_container