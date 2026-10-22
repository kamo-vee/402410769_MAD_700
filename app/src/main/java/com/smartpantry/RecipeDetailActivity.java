package com.smartpantry;

import android.os.Bundle;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smartpantry.data.PantryDatabase;
import com.smartpantry.model.Recipe;
import com.smartpantry.model.Recipe.Requirement;

import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private PantryDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        database = new PantryDatabase(this);
        long id = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe = database.getRecipe(id);
        if (recipe == null) {
            Toast.makeText(this, "Recipe not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ((TextView) findViewById(R.id.detail_category)).setText(recipe.category.toUpperCase(Locale.ROOT));
        ((TextView) findViewById(R.id.detail_name)).setText(recipe.name);
        ((TextView) findViewById(R.id.detail_description)).setText(recipe.description);
        ((TextView) findViewById(R.id.detail_time)).setText("Ready in about " + recipe.minutes + " minutes");
        NavigationUi.select(this, R.id.nav_recipes);
        findViewById(R.id.nav_pantry).setOnClickListener(v ->
                startActivity(new Intent(this, PantryActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        findViewById(R.id.nav_recipes).setOnClickListener(v -> finish());
        findViewById(R.id.nav_settings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

       
        for (Requirement requirement : recipe.ingredients) {
            TextView line = makeLine(String.format(Locale.getDefault(), "•  %s %s %s",
                    format(requirement.quantity), requirement.unit, requirement.name), 15);
            ingredients.addView(line);
        }
        LinearLayout steps = findViewById(R.id.detail_steps);
        for (int i = 0; i < recipe.steps.size(); i++) {
            steps.addView(makeLine((i + 1) + ".  " + recipe.steps.get(i), 15));
        }
        findViewById(R.id.back_to_recipes).setOnClickListener(v -> finish());
    }

    private TextView makeLine(String text, int size) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(getColor(R.color.ink));
        view.setTextSize(size);
        view.setPadding(0, 9, 0, 9);
        return view;
    }

    private String format(double value) {
        return value == Math.rint(value) ? String.format(Locale.getDefault(), "%.0f", value)
                : String.format(Locale.getDefault(), "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
