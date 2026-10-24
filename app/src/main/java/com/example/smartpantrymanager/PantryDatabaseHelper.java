package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT = "unit";

    public static final String TABLE_RECIPES = "recipes";
    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_DESCRIPTION = "description";
    public static final String RECIPE_INGREDIENTS = "ingredients";
    public static final String RECIPE_QUANTITIES = "quantities";
    public static final String RECIPE_UNITS = "units";
    public static final String RECIPE_INSTRUCTIONS = "instructions";

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL)");

        createRecipesTable(db);
    }

    private void createRecipesTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "description TEXT NOT NULL, " +
                "ingredients TEXT NOT NULL, " +
                "quantities TEXT NOT NULL, " +
                "units TEXT NOT NULL, " +
                "instructions TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createRecipesTable(db);
        }
    }

    private boolean isValidIngredient(String name, double quantity,
                                      String unit) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            return false;
        }

        if (unit == null || unit.trim().isEmpty()) {
            return false;
        }

        String selectedUnit = unit.trim();

        return selectedUnit.equals("kg")
                || selectedUnit.equals("g")
                || selectedUnit.equals("L")
                || selectedUnit.equals("ml")
                || selectedUnit.equals("pieces")
                || selectedUnit.equals("packets");
    }

    public boolean addIngredient(String name, double quantity, String unit) {
        if (!isValidIngredient(name, quantity, unit)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit.trim());

        return getWritableDatabase().insert(
                TABLE_INGREDIENTS, null, values) != -1;
    }

    public Cursor getAllIngredients() {
        return getReadableDatabase().query(
                TABLE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );
    }

    public boolean updateIngredient(int id, String name,
                                    double quantity, String unit) {
        if (id <= 0 || !isValidIngredient(name, quantity, unit)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT, unit.trim());

        return getWritableDatabase().update(
                TABLE_INGREDIENTS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    public boolean deleteIngredient(int id) {
        if (id <= 0) {
            return false;
        }

        return getWritableDatabase().delete(
                TABLE_INGREDIENTS,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    public boolean addRecipe(String name, String description,
                             String ingredients, String quantities,
                             String units, String instructions) {
        if (isBlank(name)
                || isBlank(description)
                || isBlank(ingredients)
                || isBlank(quantities)
                || isBlank(units)
                || isBlank(instructions)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(RECIPE_NAME, name.trim());
        values.put(RECIPE_DESCRIPTION, description.trim());
        values.put(RECIPE_INGREDIENTS, ingredients.trim());
        values.put(RECIPE_QUANTITIES, quantities.trim());
        values.put(RECIPE_UNITS, units.trim());
        values.put(RECIPE_INSTRUCTIONS, instructions.trim());

        return getWritableDatabase().insertWithOnConflict(
                TABLE_RECIPES,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        ) != -1;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public Cursor getAllRecipes() {
        return getReadableDatabase().query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                RECIPE_NAME + " ASC"
        );
    }
}