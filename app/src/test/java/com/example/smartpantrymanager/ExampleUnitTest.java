package com.example.smartpantrymanager;

import org.junit.Test;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void ingredientNamesIgnoreCaseAndSpaces() {
        assertTrue(
                RecipeMatcher.isSameIngredient(
                        " Tomato ",
                        "tomato"
                )
        );
    }

    @Test
    public void pluralIngredientNamesMatch() {
        assertTrue(
                RecipeMatcher.isSameIngredient(
                        "potatoes",
                        "potato"
                )
        );

        assertTrue(
                RecipeMatcher.isSameIngredient(
                        "tomatoes",
                        "tomato"
                )
        );
    }

    @Test
    public void differentIngredientsDoNotMatch() {
        assertFalse(
                RecipeMatcher.isSameIngredient(
                        "potato",
                        "tomato"
                )
        );
    }

    @Test
    public void gramsAndKilogramsAreConvertedCorrectly() {
        assertEquals(
                1000.0,
                RecipeMatcher.convertToBaseUnit(1, "kg"),
                0.001
        );

        assertEquals(
                1000.0,
                RecipeMatcher.convertToBaseUnit(1000, "g"),
                0.001
        );
    }

    @Test
    public void compatibleUnitsAreRecognised() {
        assertTrue(
                RecipeMatcher.areCompatibleUnits("kg", "g")
        );

        assertTrue(
                RecipeMatcher.areCompatibleUnits("l", "ml")
        );

        assertFalse(
                RecipeMatcher.areCompatibleUnits("kg", "ml")
        );
    }

    @Test
    public void sufficientQuantityMatches() {
        assertTrue(
                RecipeMatcher.hasEnoughQuantity(
                        1.0, "kg",
                        500.0, "g"
                )
        );
    }

    @Test
    public void insufficientQuantityDoesNotMatch() {
        assertFalse(
                RecipeMatcher.hasEnoughQuantity(
                        200.0, "g",
                        500.0, "g"
                )
        );
    }

    @Test
    public void invalidQuantitiesAreRejected() {
        assertFalse(
                RecipeMatcher.hasEnoughQuantity(
                        -1.0, "g",
                        100.0, "g"
                )
        );
    }
}