package com.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smartpantry.data.PantryDatabase;
import com.smartpantry.model.Recipe;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private PantryDatabase database;
    private TextView pantryCount;
    private TextView homeSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new PantryDatabase(this);
        pantryCount = findViewById(R.id.pantry_count);
        homeSummary = findViewById(R.id.home_summary);

        Button pantry = findViewById(R.id.open_pantry);
        Button recipes = findViewById(R.id.open_recipes);
        Button settings = findViewById(R.id.open_settings);
        pantry.setOnClickListener(v -> startActivity(new Intent(this, PantryActivity.class)));
        recipes.setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        settings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (database == null) return;
        int count = database.getPantryItems().size();
        pantryCount.setText(count + (count == 1 ? " ingredient" : " ingredients"));
        List<Recipe> matching = database.getStrictSuggestions();
        homeSummary.setText(matching.isEmpty()
                ? "No full recipe matches yet. Add ingredients to discover what you can make."
                : matching.size() + (matching.size() == 1
                    ? " recipe is ready to make with your pantry."
                    : " recipes are ready to make with your pantry."));
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
