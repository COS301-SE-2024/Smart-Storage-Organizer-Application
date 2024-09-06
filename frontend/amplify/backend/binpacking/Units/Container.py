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