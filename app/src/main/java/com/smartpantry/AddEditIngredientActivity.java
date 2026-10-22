package com.smartpantry;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smartpantry.data.PantryDatabase;
import com.smartpantry.model.PantryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {
    public static final String EXTRA_ID = "pantry_item_id";
    private static final String[] UNITS = {"pcs", "g", "kg", "ml", "l", "tsp", "tbsp", "cup"};

    private PantryDatabase database;
    private EditText nameInput;
    private EditText quantityInput;
    private EditText expiryInput;
    private Spinner unitSpinner;
    private long itemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);
        database = new PantryDatabase(this);
        nameInput = findViewById(R.id.input_name);
        quantityInput = findViewById(R.id.input_quantity);
        expiryInput = findViewById(R.id.input_expiry);
        unitSpinner = findViewById(R.id.input_unit);

        ArrayAdapter<String> units = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, UNITS);
        units.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(units);

        itemId = getIntent().getLongExtra(EXTRA_ID, -1);
        if (itemId >= 0) loadExistingItem();
        else ((TextView) findViewById(R.id.form_title)).setText("Add ingredient");

        findViewById(R.id.save_ingredient).setOnClickListener(v -> save());
        findViewById(R.id.cancel_ingredient).setOnClickListener(v -> finish());
    }

    private void loadExistingItem() {
        ((TextView) findViewById(R.id.form_title)).setText("Edit ingredient");
        for (PantryItem item : database.getPantryItems()) {
            if (item.id != itemId) continue;
            nameInput.setText(item.name);
            quantityInput.setText(String.valueOf(item.quantity));
            expiryInput.setText(item.expiryDate);
            int unitPosition = Arrays.asList(UNITS).indexOf(item.unit);
            if (unitPosition >= 0) unitSpinner.setSelection(unitPosition);
            return;
        }
        Toast.makeText(this, "Ingredient no longer exists.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void save() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String expiry = expiryInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Enter an ingredient name");
            nameInput.requestFocus();
            return;
        }
        if (name.length() > 60) {
            nameInput.setError("Use 60 characters or fewer");
            nameInput.requestFocus();
            return;
        }
        double quantity;
        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException ex) {
            quantityInput.setError("Enter a valid quantity");
            quantityInput.requestFocus();
            return;
        }
        if (Double.isNaN(quantity) || Double.isInfinite(quantity) || quantity <= 0 || quantity > 1000000) {
            quantityInput.setError("Quantity must be greater than zero");
            quantityInput.requestFocus();
            return;
        }
        if (!expiry.isEmpty() && !isValidDate(expiry)) {
            expiryInput.setError("Use a valid date in YYYY-MM-DD format");
            expiryInput.requestFocus();
            return;
        }
        try {
            String unit = unitSpinner.getSelectedItem().toString();
            if (itemId >= 0) database.updatePantryItem(itemId, name, quantity, unit, expiry);
            else database.addPantryItem(name, quantity, unit, expiry);
            Toast.makeText(this, "Ingredient saved.", Toast.LENGTH_SHORT).show();
            finish();
        } catch (RuntimeException ex) {
            Toast.makeText(this, "Could not save this ingredient.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isValidDate(String value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        format.setLenient(false);
        try {
            Date parsed = format.parse(value);
            return parsed != null && format.format(parsed).equals(value);
        } catch (ParseException ex) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        if (database != null) database.close();
        super.onDestroy();
    }
}
