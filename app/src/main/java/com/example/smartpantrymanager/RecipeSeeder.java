package com.example.smartpantrymanager;

import android.content.Context;

public class RecipeSeeder {

    public static void seedRecipes(Context context) {
        PantryDatabaseHelper db = new PantryDatabaseHelper(context);

        try {
            for (String[] recipe : RecipeData.RECIPES) {
                db.addRecipe(
                        recipe[0], recipe[1], recipe[2],
                        recipe[3], recipe[4], recipe[5]
                );
            }
        } finally {
            db.close();
        }
    }
}