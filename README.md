## Project Description
Smart Pantry Manager is an Android application developed using Java and XML in Android Studio. It helps users manage pantry ingredients and find recipes based on the ingredients and quantities they have available.

## Features
- Add pantry ingredients with a name, quantity and unit.
- View, edit and delete pantry ingredients.
- Validate ingredient names and quantities.
- Suggest recipes only when all required ingredients are available in sufficient quantities.
- Convert compatible measurement units when checking ingredient quantities.
- View recipe descriptions, ingredients and cooking instructions.
- Display feedback when no recipes match the available ingredients.
- Navigate between the application's screens.

## Database
The application uses **SQLite** to store pantry ingredients and recipes locally on the device. SQLite allows the application to save and retrieve data without requiring an external database server.

The database contains two tables:
- `ingredients` — stores ingredient names, quantities and units.
- `recipes` — stores recipe names, descriptions, required ingredients, quantities, units and cooking instructions.

## Setup Instructions
1. Install Android Studio.
2. Clone or download this GitHub repository.
3. Open the `SmartPantryManager` project in Android Studio.
4. Allow Gradle to synchronise and download any required dependencies.
5. Select an Android emulator or connected Android device.
6. Run the application.

## Testing
The application can be tested by adding, viewing, editing and deleting ingredients, checking input validation, restarting the application to verify data persistence, and testing recipe suggestions with sufficient and insufficient ingredient quantities.

