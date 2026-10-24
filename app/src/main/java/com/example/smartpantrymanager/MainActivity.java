package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        RecipeSeeder.seedRecipes(this);

        Button btnPantry = findViewById(R.id.btnPantry);
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);
        Button btnRecipes = findViewById(R.id.btnRecipes);
        Button btnSettings = findViewById(R.id.btnSettings);

        btnPantry.setOnClickListener(v ->
                openScreen(PantryActivity.class));

        btnAddIngredient.setOnClickListener(v ->
                openScreen(AddIngredientActivity.class));

        btnRecipes.setOnClickListener(v ->
                openScreen(SuggestedRecipesActivity.class));

        btnSettings.setOnClickListener(v ->
                openScreen(SettingsActivity.class));
    }

    private void openScreen(Class<?> screen) {
        Intent intent = new Intent(MainActivity.this, screen);
        startActivity(intent);
    }
}