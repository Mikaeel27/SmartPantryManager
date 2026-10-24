package com.example.smartpantrymanager;

import java.util.Locale;

public class RecipeMatcher {

    private RecipeMatcher() {}

    public static boolean isSameIngredient(String pantryName, String recipeName) {
        return normalizeIngredient(pantryName).equals(normalizeIngredient(recipeName));
    }

    private static String normalizeIngredient(String name) {
        if (name == null) return "";

        String ingredient = name.trim().toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        switch (ingredient) {
            case "potatoes": return "potato";
            case "tomatoes": return "tomato";
            case "chillies": return "chilli";
            case "berries": return "berry";
            case "leaves": return "leaf";
        }

        if (ingredient.length() > 3 && ingredient.endsWith("ies")) {
            return ingredient.substring(0, ingredient.length() - 3) + "y";
        }

        if (ingredient.length() > 3 &&
                (ingredient.endsWith("sses") ||
                        ingredient.endsWith("shes") ||
                        ingredient.endsWith("ches") ||
                        ingredient.endsWith("xes"))) {
            return ingredient.substring(0, ingredient.length() - 2);
        }

        if (ingredient.length() > 2 && ingredient.endsWith("s")
                && !ingredient.endsWith("ss")) {
            return ingredient.substring(0, ingredient.length() - 1);
        }

        return ingredient;
    }

    public static double convertToBaseUnit(double quantity, String unit) {
        if (unit == null || quantity < 0) return -1;

        switch (unit.trim().toLowerCase(Locale.ROOT)) {
            case "kg":
            case "l":
                return quantity * 1000;
            case "g":
            case "ml":
            case "piece":
            case "pieces":
                return quantity;
            default:
                return -1;
        }
    }

    public static boolean areCompatibleUnits(String pantryUnit, String recipeUnit) {
        if (pantryUnit == null || recipeUnit == null) return false;

        String pantry = pantryUnit.trim().toLowerCase(Locale.ROOT);
        String recipe = recipeUnit.trim().toLowerCase(Locale.ROOT);

        return (isWeightUnit(pantry) && isWeightUnit(recipe))
                || (isVolumeUnit(pantry) && isVolumeUnit(recipe))
                || (isPieceUnit(pantry) && isPieceUnit(recipe));
    }

    private static boolean isWeightUnit(String unit) {
        return unit.equals("g") || unit.equals("kg");
    }

    private static boolean isVolumeUnit(String unit) {
        return unit.equals("ml") || unit.equals("l");
    }

    private static boolean isPieceUnit(String unit) {
        return unit.equals("piece") || unit.equals("pieces");
    }

    public static boolean hasEnoughQuantity(double pantryQuantity, String pantryUnit,
                                            double requiredQuantity, String requiredUnit) {
        if (pantryQuantity < 0 || requiredQuantity < 0
                || !areCompatibleUnits(pantryUnit, requiredUnit)) {
            return false;
        }

        double available = convertToBaseUnit(pantryQuantity, pantryUnit);
        double required = convertToBaseUnit(requiredQuantity, requiredUnit);

        return available >= 0 && required >= 0 && available >= required;
    }
}