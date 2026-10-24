package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText editIngredientName;
    private EditText editIngredientQuantity;
    private Spinner spinnerIngredientUnit;

    private PantryDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        // Connect the screen to the database
        databaseHelper = new PantryDatabaseHelper(this);

        // Find the screen components
        editIngredientName = findViewById(R.id.editIngredientName);
        editIngredientQuantity = findViewById(R.id.editIngredientQuantity);
        spinnerIngredientUnit = findViewById(R.id.spinnerIngredientUnit);

        Button btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        Button btnCancelIngredient = findViewById(R.id.btnCancelIngredient);

        // Available measurement units
        String[] units = {"kg", "g", "L", "ml", "pieces", "packets"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerIngredientUnit.setAdapter(adapter);

        // Save the ingredient to SQLite
        btnSaveIngredient.setOnClickListener(v -> saveIngredient());

        // Return to the previous screen
        btnCancelIngredient.setOnClickListener(v -> finish());
    }

    private void saveIngredient() {
        String name = editIngredientName.getText().toString().trim();
        String quantityText =
                editIngredientQuantity.getText().toString().trim();

        // Validate the ingredient name
        if (name.isEmpty()) {
            editIngredientName.setError("Enter an ingredient name");
            editIngredientName.requestFocus();
            return;
        }

        // Validate the quantity
        if (quantityText.isEmpty()) {
            editIngredientQuantity.setError("Enter a quantity");
            editIngredientQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editIngredientQuantity.setError("Enter a valid quantity");
            editIngredientQuantity.requestFocus();
            return;
        }

        if (!Double.isFinite(quantity) || quantity <= 0) {
            editIngredientQuantity.setError(
                    "Quantity must be greater than zero"
            );
            editIngredientQuantity.requestFocus();
            return;
        }

        String unit = spinnerIngredientUnit.getSelectedItem().toString();

        // Insert the ingredient into the database
        boolean success = databaseHelper.addIngredient(
                name,
                quantity,
                unit
        );

        if (success) {
            Toast.makeText(
                    this,
                    name + " saved successfully!",
                    Toast.LENGTH_SHORT
            ).show();

            // Clear the form after saving
            editIngredientName.setText("");
            editIngredientQuantity.setText("");
            spinnerIngredientUnit.setSelection(0);
            editIngredientName.requestFocus();

        } else {
            Toast.makeText(
                    this,
                    "Could not save ingredient. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}