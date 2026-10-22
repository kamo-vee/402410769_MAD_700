package com.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS = "smart_pantry_settings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        Switch expiryAlerts = findViewById(R.id.expiry_toggle);
        expiryAlerts.setChecked(preferences.getBoolean(KEY_EXPIRY_ALERTS, true));
        expiryAlerts.setOnCheckedChangeListener((button, checked) ->
                preferences.edit().putBoolean(KEY_EXPIRY_ALERTS, checked).apply());

        NavigationUi.select(this, R.id.nav_settings);
        findViewById(R.id.nav_pantry).setOnClickListener(v ->
                startActivity(new Intent(this, PantryActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
        findViewById(R.id.nav_recipes).setOnClickListener(v ->
                startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        findViewById(R.id.nav_settings).setOnClickListener(v -> {});
    }
}
