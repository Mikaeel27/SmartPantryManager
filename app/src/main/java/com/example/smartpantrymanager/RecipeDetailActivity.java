package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_NAME = "recipe_name";

    private PantryDatabaseHelper dbHelper;

    private TextView textDetailRecipeName;
    private TextView textDetailRecipeDescription;
    private TextView textDetailRecipeIngredients;
    private TextView textDetailRecipeInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        textDetailRecipeName =
                findViewById(R.id.textDetailRecipeName);

        textDetailRecipeDescription =
                findViewById(R.id.textDetailRecipeDescription);

        textDetailRecipeIngredients =
                findViewById(R.id.textDetailRecipeIngredients);

        textDetailRecipeInstructions =
                findViewById(R.id.textDetailRecipeInstructions);

        Button btnBack =
                findViewById(R.id.btnRecipeDetailBack);

        dbHelper = new PantryDatabaseHelper(this);

        btnBack.setOnClickListener(view -> finish());

        String recipeName =
                getIntent().getStringExtra(EXTRA_RECIPE_NAME);

        if (recipeName == null || recipeName.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "No recipe was selected.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        loadRecipeDetails(recipeName);
    }

    private void loadRecipeDetails(String recipeName) {
        Cursor cursor = dbHelper.getAllRecipes();

        if (cursor == null) {
            showRecipeNotFound();
            return;
        }

        try {
            int nameCol = cursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_NAME);

            int descriptionCol = cursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_DESCRIPTION);

            int ingredientsCol = cursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_INGREDIENTS);

            int quantitiesCol = cursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_QUANTITIES);

            int unitsCol = cursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_UNITS);

            int instructionsCol = cursor.getColumnIndexOrThrow(
                    PantryDatabaseHelper.RECIPE_INSTRUCTIONS);

            while (cursor.moveToNext()) {
                String storedName = cursor.getString(nameCol);

                if (storedName.equals(recipeName)) {
                    textDetailRecipeName.setText(storedName);

                    textDetailRecipeDescription.setText(
                            cursor.getString(descriptionCol));

                    String[] ingredients =
                            cursor.getString(ingredientsCol).split("\\|");

                    String[] quantities =
                            cursor.getString(quantitiesCol).split("\\|");

                    String[] units =
                            cursor.getString(unitsCol).split("\\|");

                    StringBuilder ingredientDetails =
                            new StringBuilder();

                    int count = Math.min(
                            ingredients.length,
                            Math.min(quantities.length, units.length)
                    );

                    for (int i = 0; i < count; i++) {
                        ingredientDetails
                                .append("• ")
                                .append(capitalize(ingredients[i]))
                                .append(" — ")
                                .append(quantities[i])
                                .append(" ")
                                .append(units[i])
                                .append("\n");
                    }

                    textDetailRecipeIngredients.setText(
                            ingredientDetails.toString().trim());

                    textDetailRecipeInstructions.setText(
                            cursor.getString(instructionsCol));

                    return;
                }
            }

            showRecipeNotFound();

        } finally {
            cursor.close();
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return text.substring(0, 1).toUpperCase()
                + text.substring(1);
    }

    private void showRecipeNotFound() {
        Toast.makeText(
                this,
                "Recipe details could not be found.",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}