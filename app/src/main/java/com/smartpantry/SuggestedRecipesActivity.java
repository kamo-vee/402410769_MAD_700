package com.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.adapter.RecipeAdapter;
import com.smartpantry.data.PantryDatabase;
import com.smartpantry.model.Recipe;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private PantryDatabase database;
    private RecipeAdapter adapter;
    private TextView emptyMessage;
    private TextView recipeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        database = new PantryDatabase(this);
        emptyMessage = findViewById(R.id.no_matches);
        recipeCount = findViewById(R.id.recipe_count);
        RecyclerView recycler = findViewById(R.id.recipes_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setNestedScrollingEnabled(true);
        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(this, RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.id);
            startActivity(intent);
        });
        recycler.setAdapter(adapter);

        NavigationUi.select(this, R.id.nav_recipes);
        findViewById(R.id.nav_pantry).setOnClickListener(v ->
                startActivity(new Intent(this, PantryActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        findViewById(R.id.nav_recipes).setOnClickListener(v -> {});
        findViewById(R.id.nav_settings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> matches = database.getStrictSuggestions();
        adapter.replaceRecipes(matches);
        recipeCount.setText(matches.size() + (matches.size() == 1
                ? " recipe matches every ingredient and quantity."
                : " recipes match every ingredient and quantity."));
        emptyMessage.setVisibility(matches.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
