package com.example.smartpantrymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PantryAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> ingredients;

    public PantryAdapter(Context context, List<String> ingredients) {
        super(context, android.R.layout.simple_list_item_1, ingredients);

        this.context = context;
        this.ingredients = ingredients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    android.R.layout.simple_list_item_1,
                    parent,
                    false
            );
        }

        TextView ingredientText = view.findViewById(android.R.id.text1);

        ingredientText.setText(ingredients.get(position));
        ingredientText.setTextSize(16);
        ingredientText.setPadding(16, 20, 16, 20);

        return view;
    }
}