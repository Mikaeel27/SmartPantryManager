package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PantryActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private ListView listPantryIngredients;
    private TextView textEmptyPantry;
    private TextView textPantrySubtitle;

    private final ArrayList<String> ingredientList = new ArrayList<>();
    private final ArrayList<Integer> ingredientIds = new ArrayList<>();

    private PantryAdapter adapter;

    private static final String[] UNITS = {
            "kg", "g", "L", "ml", "pieces", "packets"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        databaseHelper = new PantryDatabaseHelper(this);

        listPantryIngredients =
                findViewById(R.id.listPantryIngredients);
        textEmptyPantry = findViewById(R.id.textEmptyPantry);
        textPantrySubtitle = findViewById(R.id.textPantrySubtitle);

        Button btnAddIngredient =
                findViewById(R.id.btnPantryAddIngredient);
        Button btnBack = findViewById(R.id.btnPantryBack);

        adapter = new PantryAdapter(this, ingredientList);
        listPantryIngredients.setAdapter(adapter);

        btnAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });

        btnBack.setOnClickListener(view -> finish());

        listPantryIngredients.setOnItemClickListener(
                (parent, view, position, id) -> showEditDialog(position)
        );

        listPantryIngredients.setOnItemLongClickListener(
                (parent, view, position, id) -> {
                    confirmDelete(position);
                    return true;
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        ingredientList.clear();
        ingredientIds.clear();

        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            if (cursor != null) {
                int idIndex = cursor.getColumnIndexOrThrow(
                        PantryDatabaseHelper.COLUMN_ID
                );
                int nameIndex = cursor.getColumnIndexOrThrow(
                        PantryDatabaseHelper.COLUMN_NAME
                );
                int quantityIndex = cursor.getColumnIndexOrThrow(
                        PantryDatabaseHelper.COLUMN_QUANTITY
                );
                int unitIndex = cursor.getColumnIndexOrThrow(
                        PantryDatabaseHelper.COLUMN_UNIT
                );

                while (cursor.moveToNext()) {
                    int ingredientId = cursor.getInt(idIndex);
                    String name = cursor.getString(nameIndex);
                    double quantity = cursor.getDouble(quantityIndex);
                    String unit = cursor.getString(unitIndex);

                    ingredientIds.add(ingredientId);
                    ingredientList.add(
                            name + "\nQuantity: " + quantity + " " + unit
                    );
                }
            }
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not load pantry ingredients.",
                    Toast.LENGTH_LONG
            ).show();
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        boolean isEmpty = ingredientList.isEmpty();

        listPantryIngredients.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE
        );
        textEmptyPantry.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE
        );
        textPantrySubtitle.setText(
                ingredientList.size() + " ingredient(s) saved"
        );
    }

    private void showEditDialog(int position) {
        if (!isValidPosition(position)) {
            loadIngredients();
            return;
        }

        int ingredientId = ingredientIds.get(position);

        try (Cursor cursor = databaseHelper.getReadableDatabase().query(
                PantryDatabaseHelper.TABLE_INGREDIENTS,
                null,
                PantryDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(ingredientId)},
                null,
                null,
                null
        )) {
            if (cursor == null || !cursor.moveToFirst()) {
                Toast.makeText(
                        this,
                        "Ingredient not found.",
                        Toast.LENGTH_SHORT
                ).show();
                loadIngredients();
                return;
            }

            String currentName = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            PantryDatabaseHelper.COLUMN_NAME
                    )
            );

            double currentQuantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(
                            PantryDatabaseHelper.COLUMN_QUANTITY
                    )
            );

            String currentUnit = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            PantryDatabaseHelper.COLUMN_UNIT
                    )
            );

            EditText nameInput = new EditText(this);
            nameInput.setHint("Ingredient name");
            nameInput.setSingleLine(true);
            nameInput.setText(currentName);

            EditText quantityInput = new EditText(this);
            quantityInput.setHint("Quantity");
            quantityInput.setInputType(
                    InputType.TYPE_CLASS_NUMBER
                            | InputType.TYPE_NUMBER_FLAG_DECIMAL
                            | InputType.TYPE_NUMBER_FLAG_SIGNED
            );
            quantityInput.setText(String.valueOf(currentQuantity));

            Spinner unitSpinner = new Spinner(this);

            ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    UNITS
            );

            unitAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );

            unitSpinner.setAdapter(unitAdapter);

            for (int i = 0; i < UNITS.length; i++) {
                if (UNITS[i].equals(currentUnit)) {
                    unitSpinner.setSelection(i);
                    break;
                }
            }

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            int padding = (int) (
                    20 * getResources().getDisplayMetrics().density
            );

            layout.setPadding(padding, 0, padding, 0);
            layout.addView(nameInput);
            layout.addView(quantityInput);
            layout.addView(unitSpinner);

            AlertDialog editDialog = new AlertDialog.Builder(this)
                    .setTitle("Edit Ingredient")
                    .setView(layout)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Save", null)
                    .create();

            editDialog.setOnShowListener(dialog -> {
                Button saveButton = editDialog.getButton(
                        AlertDialog.BUTTON_POSITIVE
                );

                saveButton.setOnClickListener(view -> {
                    String newName = nameInput.getText()
                            .toString().trim();

                    String quantityText = quantityInput.getText()
                            .toString().trim();

                    if (newName.isEmpty()) {
                        nameInput.setError(
                                "Enter an ingredient name"
                        );
                        nameInput.requestFocus();
                        return;
                    }

                    if (quantityText.isEmpty()) {
                        quantityInput.setError("Enter a quantity");
                        quantityInput.requestFocus();
                        return;
                    }

                    final double newQuantity;

                    try {
                        newQuantity = Double.parseDouble(quantityText);
                    } catch (NumberFormatException e) {
                        quantityInput.setError(
                                "Enter a valid quantity"
                        );
                        quantityInput.requestFocus();
                        return;
                    }

                    if (!Double.isFinite(newQuantity)
                            || newQuantity <= 0) {
                        quantityInput.setError(
                                "Quantity must be greater than zero"
                        );
                        quantityInput.requestFocus();
                        return;
                    }

                    if (unitSpinner.getSelectedItem() == null) {
                        Toast.makeText(
                                this,
                                "Please select a measurement unit.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    boolean updated;

                    try {
                        updated = databaseHelper.updateIngredient(
                                ingredientId,
                                newName,
                                newQuantity,
                                unitSpinner.getSelectedItem().toString()
                        );
                    } catch (Exception e) {
                        updated = false;
                    }

                    if (updated) {
                        Toast.makeText(
                                this,
                                "Ingredient updated successfully.",
                                Toast.LENGTH_SHORT
                        ).show();

                        editDialog.dismiss();
                        loadIngredients();
                    } else {
                        Toast.makeText(
                                this,
                                "Could not update ingredient. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            });

            editDialog.show();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not open ingredient for editing.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void confirmDelete(int position) {
        if (!isValidPosition(position)) {
            loadIngredients();
            return;
        }

        int ingredientId = ingredientIds.get(position);
        String ingredient = ingredientList.get(position);

        new AlertDialog.Builder(this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Are you sure you want to delete this ingredient?\n\n"
                                + ingredient
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean deleted;

                    try {
                        deleted = databaseHelper.deleteIngredient(
                                ingredientId
                        );
                    } catch (Exception e) {
                        deleted = false;
                    }

                    Toast.makeText(
                            this,
                            deleted
                                    ? "Ingredient deleted."
                                    : "Could not delete ingredient.",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadIngredients();
                })
                .show();
    }

    private boolean isValidPosition(int position) {
        return position >= 0
                && position < ingredientIds.size()
                && position < ingredientList.size();
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}