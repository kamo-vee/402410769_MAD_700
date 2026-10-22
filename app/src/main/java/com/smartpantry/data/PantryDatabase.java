package com.smartpantry.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.smartpantry.model.PantryItem;
import com.smartpantry.model.Recipe;
import com.smartpantry.model.Recipe.Requirement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Local persistence for pantry records and the preloaded recipe collection.
 * Recipe matching is deliberately strict: every required ingredient and amount
 * must be available before a recipe is returned.
 */
public class PantryDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public PantryDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL COLLATE NOCASE," +
                "quantity REAL NOT NULL CHECK(quantity > 0)," +
                "unit TEXT NOT NULL," +
                "expiry_date TEXT)");
        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "category TEXT NOT NULL," +
                "minutes INTEGER NOT NULL," +
                "ingredients_json TEXT NOT NULL," +
                "steps_json TEXT NOT NULL)");

        seedPantry(db);
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Versioned schema changes should be migrated here rather than dropping user data.
    }

    private void seedPantry(SQLiteDatabase db) {
        addSeedPantry(db, "Tomato", 4, "pcs", "");
        addSeedPantry(db, "Onion", 2, "pcs", "");
        addSeedPantry(db, "Egg", 6, "pcs", "");
        addSeedPantry(db, "Rice", 500, "g", "");
        addSeedPantry(db, "Pasta", 500, "g", "");
        addSeedPantry(db, "Olive oil", 100, "ml", "");
        addSeedPantry(db, "Salt", 50, "g", "");
        addSeedPantry(db, "Black pepper", 10, "g", "");
        addSeedPantry(db, "Potato", 800, "g", "");
        addSeedPantry(db, "Cheese", 150, "g", "");
        addSeedPantry(db, "Spinach", 120, "g", "");
    }

    private void addSeedPantry(SQLiteDatabase db, String name, double quantity,
                               String unit, String expiry) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiry);
        db.insert("pantry_items", null, values);
    }

    private void seedRecipes(SQLiteDatabase db) {
        addSeedRecipe(db, "Tomato & onion omelette", "A soft, savoury omelette with a fresh tomato filling.",
                "Breakfast", 15, req("Egg|2|pcs", "Tomato|1|pcs", "Onion|0.5|pcs", "Olive oil|10|ml", "Salt|2|g"),
                "Beat the eggs with salt.|Dice the tomato and onion.|Warm oil in a pan and cook the vegetables.|Pour in the eggs and cook until set.");
        addSeedRecipe(db, "Simple tomato pasta", "A quick tomato and onion sauce tossed through pasta.",
                "Dinner", 25, req("Pasta|180|g", "Tomato|2|pcs", "Onion|0.5|pcs", "Olive oil|15|ml", "Salt|3|g"),
                "Boil pasta until tender, then drain.|Dice tomato and onion.|Cook the onion in oil, add tomato and salt, and simmer.|Toss the sauce through the pasta.");
        addSeedRecipe(db, "Golden potato hash", "Crisp-edged potatoes with soft onion and pepper.",
                "Lunch", 25, req("Potato|300|g", "Onion|0.5|pcs", "Olive oil|15|ml", "Salt|2|g", "Black pepper|1|g"),
                "Dice the potatoes and onion.|Heat oil in a pan.|Cook the potatoes until browned and tender.|Add onion, salt, and pepper and cook until soft.");
        addSeedRecipe(db, "Cheese & spinach omelette", "A filling omelette folded around spinach and cheese.",
                "Breakfast", 15, req("Egg|2|pcs", "Spinach|40|g", "Cheese|30|g", "Olive oil|10|ml", "Black pepper|1|g"),
                "Beat the eggs with pepper.|Wilt spinach in a lightly oiled pan.|Pour in egg and cook gently.|Add cheese, fold, and serve when set.");
        addSeedRecipe(db, "Tomato rice bowl", "Comforting rice with a lightly cooked tomato topping.",
                "Lunch", 30, req("Rice|150|g", "Tomato|2|pcs", "Onion|0.5|pcs", "Olive oil|10|ml", "Salt|2|g"),
                "Rinse and cook the rice.|Dice the tomato and onion.|Cook onion in oil, then add tomato and salt.|Spoon the tomato mixture over the rice.");
        addSeedRecipe(db, "Potato & cheese bake", "A simple layered bake with a golden cheese top.",
                "Dinner", 45, req("Potato|400|g", "Cheese|60|g", "Onion|0.5|pcs", "Salt|2|g", "Black pepper|1|g"),
                "Heat the oven to 190 degrees Celsius.|Thinly slice potatoes and onion.|Layer in a baking dish with seasoning.|Top with cheese and bake until tender and golden.");
        addSeedRecipe(db, "Spinach tomato pasta", "A bright pasta dish with greens and juicy tomatoes.",
                "Dinner", 25, req("Pasta|180|g", "Spinach|50|g", "Tomato|1|pcs", "Olive oil|12|ml", "Salt|2|g"),
                "Boil and drain the pasta.|Warm oil and cook chopped tomato.|Add spinach and cook until wilted.|Stir through pasta and salt.");
        addSeedRecipe(db, "Peppery tomato egg scramble", "A fast skillet breakfast with tomato and black pepper.",
                "Breakfast", 12, req("Egg|2|pcs", "Tomato|1|pcs", "Olive oil|8|ml", "Salt|1|g", "Black pepper|1|g"),
                "Chop the tomato and beat the eggs.|Warm oil and soften the tomato.|Add eggs and stir gently until softly set.|Season and serve.");
        addSeedRecipe(db, "Onion fried rice", "A practical way to turn cooked rice into a warm, savoury meal.",
                "Lunch", 20, req("Rice|180|g", "Egg|1|pcs", "Onion|0.5|pcs", "Olive oil|12|ml", "Salt|2|g"),
                "Cook rice and let it cool slightly.|Dice the onion and beat the egg.|Cook onion in oil, then scramble the egg.|Add rice and salt; stir-fry until hot.");
        addSeedRecipe(db, "Tomato cheese toast filling", "A warm tomato and cheese topping for bread if available.",
                "Snack", 15, req("Tomato|1|pcs", "Cheese|35|g", "Black pepper|1|g", "Salt|1|g"),
                "Dice tomato and grate cheese.|Place tomato and cheese on bread if you have it, or warm them together in a small pan.|Season with salt and pepper and cook until the cheese melts.");
        addSeedRecipe(db, "Spinach rice skillet", "A one-pan rice supper finished with leafy spinach.",
                "Dinner", 30, req("Rice|150|g", "Spinach|45|g", "Onion|0.5|pcs", "Olive oil|12|ml", "Salt|2|g"),
                "Cook the rice until tender.|Soften sliced onion in oil.|Add spinach and stir until wilted.|Fold in the rice and salt.");
        addSeedRecipe(db, "Cheesy potato hash", "Pan-fried potatoes finished with melted cheese.",
                "Lunch", 30, req("Potato|300|g", "Cheese|40|g", "Olive oil|15|ml", "Salt|2|g", "Black pepper|1|g"),
                "Dice and parboil potatoes until just tender.|Drain and pan-fry in oil until crisp.|Season with salt and pepper.|Scatter over cheese and cover until melted.");
        addSeedRecipe(db, "Tomato spinach skillet", "A quick warm side or light meal of tomatoes and greens.",
                "Lunch", 15, req("Tomato|2|pcs", "Spinach|50|g", "Onion|0.5|pcs", "Olive oil|10|ml", "Salt|2|g"),
                "Slice the onion and dice the tomato.|Cook onion in oil until soft.|Add tomato and simmer briefly.|Stir in spinach and salt until the leaves wilt.");
        addSeedRecipe(db, "Egg fried rice", "Rice and egg make a satisfying pantry staple.",
                "Dinner", 20, req("Rice|180|g", "Egg|2|pcs", "Olive oil|12|ml", "Salt|2|g", "Black pepper|1|g"),
                "Cook the rice and set aside.|Beat the eggs with pepper.|Scramble eggs in warm oil.|Add rice and salt; stir-fry until heated through.");
        addSeedRecipe(db, "Peppery tomato pasta", "A short ingredient list with a punch of black pepper.",
                "Dinner", 25, req("Pasta|180|g", "Tomato|2|pcs", "Olive oil|15|ml", "Black pepper|1|g", "Salt|2|g"),
                "Cook pasta in salted water and drain.|Chop tomatoes.|Cook tomatoes in oil until saucy.|Season with pepper and toss through the pasta.");
        addSeedRecipe(db, "Spinach egg rice", "A warm bowl combining rice, spinach, and soft egg.",
                "Lunch", 25, req("Rice|150|g", "Egg|1|pcs", "Spinach|40|g", "Olive oil|10|ml", "Salt|2|g"),
                "Cook the rice until tender.|Wilt spinach in a little oil.|Fry or scramble the egg.|Serve egg and spinach over rice with salt.");
        addSeedRecipe(db, "Cheese tomato omelette", "A classic omelette with juicy tomato and melted cheese.",
                "Breakfast", 15, req("Egg|2|pcs", "Tomato|1|pcs", "Cheese|30|g", "Olive oil|8|ml", "Salt|2|g"),
                "Beat eggs with salt.|Chop the tomato and grate cheese.|Cook tomato briefly in oil.|Add egg, scatter cheese over the top, and fold when set.");
        addSeedRecipe(db, "Potato spinach skillet", "Tender potatoes with wilted spinach and black pepper.",
                "Dinner", 30, req("Potato|250|g", "Spinach|40|g", "Olive oil|15|ml", "Salt|2|g", "Black pepper|1|g"),
                "Dice and boil potatoes until tender.|Drain and crisp the potatoes in oil.|Stir in spinach until wilted.|Season with salt and black pepper.");
    }

    private JSONArray req(String... encoded) {
        JSONArray result = new JSONArray();
        for (String value : encoded) {
            String[] part = value.split("\\|");
            JSONObject item = new JSONObject();
            try {
                item.put("name", part[0]);
                item.put("quantity", Double.parseDouble(part[1]));
                item.put("unit", part[2]);
                result.put(item);
            } catch (JSONException ignored) {
                // The fixed seed values are valid; skip a malformed seed row.
            }
        }
        return result;
    }

    private void addSeedRecipe(SQLiteDatabase db, String name, String description,
                               String category, int minutes, JSONArray ingredients, String steps) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("category", category);
        values.put("minutes", minutes);
        values.put("ingredients_json", ingredients.toString());
        values.put("steps_json", new JSONArray(Arrays.asList(steps.split("\\|"))).toString());
        db.insert("recipes", null, values);
    }

    public List<PantryItem> getPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query("pantry_items", null, null, null,
                null, null, "name COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                items.add(new PantryItem(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))));
            }
        }
        return items;
    }

    public long addPantryItem(String name, double quantity, String unit, String expiry) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiry == null ? "" : expiry.trim());
        return getWritableDatabase().insertOrThrow("pantry_items", null, values);
    }

    public int updatePantryItem(long id, String name, double quantity, String unit, String expiry) {
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiry == null ? "" : expiry.trim());
        return getWritableDatabase().update("pantry_items", values, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry_items", "id = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Recipe> getRecipes() {
        return readRecipes(false);
    }

    public Recipe getRecipe(long id) {
        List<Recipe> rows = readRecipes(true, id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private List<Recipe> readRecipes(boolean one, long... id) {
        List<Recipe> recipes = new ArrayList<>();
        String selection = one ? "id = ?" : null;
        String[] args = one ? new String[]{String.valueOf(id[0])} : null;
        try (Cursor cursor = getReadableDatabase().query("recipes", null, selection, args,
                null, null, "name COLLATE NOCASE ASC")) {
            while (cursor.moveToNext()) {
                recipes.add(new Recipe(cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("minutes")),
                        Recipe.parseIngredients(cursor.getString(cursor.getColumnIndexOrThrow("ingredients_json"))),
                        Recipe.parseSteps(cursor.getString(cursor.getColumnIndexOrThrow("steps_json")))));
            }
        }
        return recipes;
    }

    public List<Recipe> getStrictSuggestions() {
        List<Recipe> matching = new ArrayList<>();
        List<PantryItem> pantry = getPantryItems();
        for (Recipe recipe : getRecipes()) {
            boolean everyIngredientAvailable = true;
            for (Requirement required : recipe.ingredients) {
                double available = totalAvailable(pantry, required.name, required.unit);
                if (available + 0.000001d < toBase(required.quantity, required.unit)) {
                    everyIngredientAvailable = false;
                    break;
                }
            }
            if (everyIngredientAvailable) matching.add(recipe);
        }
        return matching;
    }

    private double totalAvailable(List<PantryItem> pantry, String name, String requiredUnit) {
        double total = 0;
        String canonicalRequired = canonicalName(name);
        for (PantryItem item : pantry) {
            if (!canonicalRequired.equals(canonicalName(item.name))) continue;
            Double base = toBaseIfCompatible(item.quantity, item.unit, requiredUnit);
            if (base != null) total += base;
        }
        return total;
    }

    private String canonicalName(String input) {
        String value = input == null ? "" : input.trim().toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
        if (value.endsWith("ies") && value.length() > 3) value = value.substring(0, value.length() - 3) + "y";
        else if (value.endsWith("oes") && value.length() > 3) value = value.substring(0, value.length() - 2);
        else if (value.endsWith("s") && value.length() > 3 && !value.endsWith("ss")) value = value.substring(0, value.length() - 1);
        return value;
    }

    private Double toBaseIfCompatible(double amount, String sourceUnit, String targetUnit) {
        String sourceDimension = dimension(sourceUnit);
        String targetDimension = dimension(targetUnit);
        if (!sourceDimension.equals(targetDimension)) return null;
        return toBase(amount, sourceUnit);
    }

    private double toBase(double amount, String unit) {
        String normalized = unit.trim().toLowerCase(Locale.ROOT);
        switch (normalized) {
            case "kg": return amount * 1000.0d;
            case "mg": return amount / 1000.0d;
            case "l": return amount * 1000.0d;
            case "tsp": return amount * 5.0d;
            case "tbsp": return amount * 15.0d;
            case "cup": return amount * 240.0d;
            case "pcs":
            case "pc":
            case "piece":
            case "pieces":
            case "item":
            case "items": return amount;
            default: return amount;
        }
    }

    private String dimension(String unit) {
        String normalized = unit.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("g") || normalized.equals("kg") || normalized.equals("mg")) return "mass";
        if (normalized.equals("ml") || normalized.equals("l") || normalized.equals("tsp")
                || normalized.equals("tbsp") || normalized.equals("cup")) return "volume";
        if (normalized.equals("pcs") || normalized.equals("pc") || normalized.equals("piece")
                || normalized.equals("pieces") || normalized.equals("item") || normalized.equals("items")) return "count";
        return "unit:" + normalized;
    }
}
