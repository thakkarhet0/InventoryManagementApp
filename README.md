# Inventory Management
An Android application that helps businesses manage and track their warehouse inventory.

## Description
The app utilizes Firebase, a popular backend as a service platform, to store and sync data across multiple devices in real-time.

The app uses Firebase Realtime Database to store information about the inventory items, including the item name, quantity, location, and other relevant details. Users can add, edit, and delete items from the inventory, and the changes will be instantly synced across all devices that have the app installed and are connected to the same Firebase project.

The app also uses Firebase Authentication to secure the data and ensure that only authorized users can access and manage the inventory. Users can sign in using their email and password.

The app also includes a search function, which allows users to quickly find and view details about specific items in the inventory. Users can filter items by category, and pagination is used to display a set number of items per page.

The app also makes use of Firebase Cloud Messaging (FCM) to send notifications to users when the inventory levels of a certain item fall below a certain threshold. This feature can be used to alert users when it's time to reorder items, or when an item is running low in stock.
