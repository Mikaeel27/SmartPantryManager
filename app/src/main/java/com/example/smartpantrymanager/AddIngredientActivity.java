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

        databaseHelper = new PantryDatabaseHelper(this);

        editIngredientName = findViewById(R.id.editIngredientName);
        editIngredientQuantity =
                findViewById(R.id.editIngredientQuantity);
        spinnerIngredientUnit =
                findViewById(R.id.spinnerIngredientUnit);

        Button btnSaveIngredient =
                findViewById(R.id.btnSaveIngredient);
        Button btnCancelIngredient =
                findViewById(R.id.btnCancelIngredient);

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

        btnSaveIngredient.setOnClickListener(v -> saveIngredient());

        btnCancelIngredient.setOnClickListener(v -> finish());
    }

    private void saveIngredient() {
        String name = editIngredientName.getText()
                .toString()
                .trim();

        String quantityText = editIngredientQuantity.getText()
                .toString()
                .trim();

        // Validate ingredient name.
        if (name.isEmpty()) {
            editIngredientName.setError(
                    "Enter an ingredient name"
            );
            editIngredientName.requestFocus();
            return;
        }

        // Validate quantity input.
        if (quantityText.isEmpty()) {
            editIngredientQuantity.setError(
                    "Enter a quantity"
            );
            editIngredientQuantity.requestFocus();
            return;
        }

        final double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            editIngredientQuantity.setError(
                    "Enter a valid number, e.g. 2 or 0.5"
            );
            editIngredientQuantity.requestFocus();
            return;
        }

        // Reject zero, negative and non-finite quantities.
        if (!Double.isFinite(quantity) || quantity <= 0) {
            editIngredientQuantity.setError(
                    "Quantity must be greater than zero"
            );
            editIngredientQuantity.requestFocus();
            return;
        }

        if (spinnerIngredientUnit.getSelectedItem() == null) {
            Toast.makeText(
                    this,
                    "Please select a measurement unit.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String unit = spinnerIngredientUnit
                .getSelectedItem()
                .toString();

        // Save the ingredient in SQLite.
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

            // Clear the form after a successful save.
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
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}