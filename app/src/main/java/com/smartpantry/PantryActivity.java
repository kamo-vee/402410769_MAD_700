package com.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.adapter.PantryAdapter;
import com.smartpantry.data.PantryDatabase;
import com.smartpantry.model.PantryItem;

public class PantryActivity extends AppCompatActivity {
    private PantryDatabase database;
    private PantryAdapter adapter;
    private TextView pantryCount;
    private TextView recipeCount;
    private TextView pantryEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        database = new PantryDatabase(this);
        pantryCount = findViewById(R.id.pantry_count);
        recipeCount = findViewById(R.id.recipe_count);
        pantryEmpty = findViewById(R.id.pantry_empty);
        RecyclerView recycler = findViewById(R.id.pantry_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setNestedScrollingEnabled(true);
        adapter = new PantryAdapter(new PantryAdapter.Actions() {
            @Override
            public void onEdit(PantryItem item) {
                Intent intent = new Intent(PantryActivity.this, AddEditIngredientActivity.class);
                intent.putExtra(AddEditIngredientActivity.EXTRA_ID, item.id);
                startActivity(intent);
            }

            @Override
            public void onDelete(PantryItem item) {
                new AlertDialog.Builder(PantryActivity.this)
                        .setTitle("Delete " + item.name + "?")
                        .setMessage("This ingredient will be removed from your pantry.")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete", (dialog, which) -> {
                            database.deletePantryItem(item.id);
                            refreshItems();
                        }).show();
            }
        });
        recycler.setAdapter(adapter);
        EditText search = findViewById(R.id.pantry_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
                updateEmptyState();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        findViewById(R.id.add_ingredient).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
        NavigationUi.select(this, R.id.nav_pantry);
        findViewById(R.id.nav_pantry).setOnClickListener(v -> {});
        findViewById(R.id.nav_recipes).setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        findViewById(R.id.nav_settings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshItems();
    }

    private void refreshItems() {
        if (adapter == null || database == null) return;
        int pantryTotal = database.getPantryItems().size();
        int recipeTotal = database.getStrictSuggestions().size();
        pantryCount.setText(String.format(java.util.Locale.ROOT, "%02d", pantryTotal));
        recipeCount.setText(String.format(java.util.Locale.ROOT, "%02d", recipeTotal));
        adapter.replaceItems(database.getPantryItems());
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (adapter.getItemCount() > 0) {
            pantryEmpty.setVisibility(View.GONE);
            return;
        }
        boolean pantryIsEmpty = database.getPantryItems().isEmpty();
        pantryEmpty.setText(pantryIsEmpty
                ? "Your pantry is ready for a fresh start. Add an ingredient to begin."
                : "Nothing by that name. Try another search.");
        pantryEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
