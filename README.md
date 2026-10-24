## Project Description
Smart Pantry Manager is an Android application developed using Java and XML in Android Studio. It allows users to manage pantry ingredients and find recipes based on available ingredients and quantities.

## Features
- Add, view, edit and delete pantry ingredients.
- Validate ingredient names and quantities.
- Store ingredients and recipes using SQLite.
- Suggest recipes only when all required ingredients are available in sufficient quantities.
- Convert compatible measurement units when matching recipes.
- View recipe descriptions, ingredients and cooking instructions.
- Display feedback when no recipes match.

## Technologies Used
- Java
- XML
- Android Studio
- SQLite
- Git and GitHub

## Database
The application uses SQLite to store data locally in two tables:
- `ingredients` — stores ingredient names, quantities and units.
- `recipes` — stores recipe names, descriptions, required ingredients, quantities, units and instructions.

## Setup Instructions
1. Install Android Studio.
2. Clone or download this repository.
3. Open the project in Android Studio.
4. Allow Gradle to synchronise.
5. Select an Android emulator or connected device.
6. Run the application.

## Testing
Test the application by:
- Adding, editing, viewing and deleting ingredients.
- Checking validation for empty names and invalid quantities.
- Restarting the application to verify data persistence.
- Testing recipe suggestions with missing ingredients and insufficient quantities.
- Checking compatible measurement conversions.

## GitHub Repository
https://github.com/Mikaeel27/SmartPantryManager