package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private ListView listSuggestedRecipes;
    private TextView textRecipeInstructions;
    private PantryDatabaseHelper dbHelper;
    private final ArrayList<String> recipeNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        listSuggestedRecipes = findViewById(R.id.listSuggestedRecipes);
        textRecipeInstructions = findViewById(R.id.textRecipeInstructions);
        Button btnRecipesBack = findViewById(R.id.btnRecipesBack);

        dbHelper = new PantryDatabaseHelper(this);
        RecipeSeeder.seedRecipes(this);

        btnRecipesBack.setOnClickListener(view -> finish());

        listSuggestedRecipes.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(
                    SuggestedRecipesActivity.this,
                    RecipeDetailActivity.class
            );
            intent.putExtra(
                    RecipeDetailActivity.EXTRA_RECIPE_NAME,
                    recipeNames.get(position)
            );
            startActivity(intent);
        });

        loadMatchingRecipes();
    }

    private void loadMatchingRecipes() {
        recipeNames.clear();

        try (Cursor pantryCursor = dbHelper.getAllIngredients();
             Cursor recipeCursor = dbHelper.getAllRecipes()) {

            if (pantryCursor == null || recipeCursor == null) {
                textRecipeInstructions.setText(
                        "Unable to load pantry ingredients or recipes."
                );
                listSuggestedRecipes.setAdapter(null);
                return;
            }

            ArrayList<String> pantryNames = new ArrayList<>();
            ArrayList<Double> pantryQuantities = new ArrayList<>();
            ArrayList<String> pantryUnits = new ArrayList<>();

            int pantryNameCol = pantryCursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.COLUMN_NAME);
            int pantryQuantityCol = pantryCursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.COLUMN_QUANTITY);
            int pantryUnitCol = pantryCursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.COLUMN_UNIT);

            while (pantryCursor.moveToNext()) {
                pantryNames.add(pantryCursor.getString(pantryNameCol));
                pantryQuantities.add(pantryCursor.getDouble(pantryQuantityCol));
                pantryUnits.add(pantryCursor.getString(pantryUnitCol));
            }

            int nameCol = recipeCursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_NAME);
            int ingredientsCol = recipeCursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_INGREDIENTS);
            int quantitiesCol = recipeCursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_QUANTITIES);
            int unitsCol = recipeCursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_UNITS);

            while (recipeCursor.moveToNext()) {
                String name = recipeCursor.getString(nameCol);
                String[] ingredients = recipeCursor.getString(ingredientsCol)
                        .split("\\|");
                String[] quantities = recipeCursor.getString(quantitiesCol)
                        .split("\\|");
                String[] units = recipeCursor.getString(unitsCol)
                        .split("\\|");

                if (ingredients.length == 0
                        || ingredients.length != quantities.length
                        || ingredients.length != units.length) {
                    continue;
                }

                boolean canMake = true;

                for (int i = 0; i < ingredients.length; i++) {
                    double requiredQuantity;

                    try {
                        requiredQuantity = Double.parseDouble(quantities[i]);
                    } catch (NumberFormatException exception) {
                        canMake = false;
                        break;
                    }

                    if (requiredQuantity < 0) {
                        canMake = false;
                        break;
                    }

                    double totalAvailable = 0;

                    for (int j = 0; j < pantryNames.size(); j++) {
                        if (RecipeMatcher.isSameIngredient(
                                pantryNames.get(j), ingredients[i])
                                && RecipeMatcher.areCompatibleUnits(
                                pantryUnits.get(j), units[i])) {

                            double available = RecipeMatcher.convertToBaseUnit(
                                    pantryQuantities.get(j), pantryUnits.get(j));

                            if (available >= 0) {
                                totalAvailable += available;
                            }
                        }
                    }

                    double required = RecipeMatcher.convertToBaseUnit(
                            requiredQuantity, units[i]);

                    if (required < 0 || totalAvailable < required) {
                        canMake = false;
                        break;
                    }
                }

                if (canMake) {
                    recipeNames.add(name);
                }
            }

        } catch (Exception exception) {
            textRecipeInstructions.setText(
                    "Unable to load recipes. Please try again."
            );
            listSuggestedRecipes.setAdapter(null);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recipeNames
        );
        listSuggestedRecipes.setAdapter(adapter);

        if (recipeNames.isEmpty()) {
            textRecipeInstructions.setText(
                    "No recipes can be made with your current pantry. "
                            + "Add the missing ingredients or check quantities."
            );
        } else {
            textRecipeInstructions.setText(
                    recipeNames.size() + " recipe(s) match your pantry. "
                            + "Tap a recipe to view its details."
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (dbHelper != null && listSuggestedRecipes != null) {
            loadMatchingRecipes();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}