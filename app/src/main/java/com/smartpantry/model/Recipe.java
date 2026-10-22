package com.smartpantry.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    public final long id;
    public final String name;
    public final String description;
    public final String category;
    public final int minutes;
    public final List<Requirement> ingredients;
    public final List<String> steps;

    public Recipe(long id, String name, String description, String category, int minutes,
                  List<Requirement> ingredients, List<String> steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.minutes = minutes;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public static List<Requirement> parseIngredients(String json) {
        List<Requirement> result = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                result.add(new Requirement(item.getString("name"),
                        item.getDouble("quantity"), item.getString("unit")));
            }
        } catch (JSONException ignored) {
            // Invalid stored seed data should not crash recipe browsing.
        }
        return result;
    }

    public static List<String> parseSteps(String json) {
        List<String> result = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) result.add(array.getString(i));
        } catch (JSONException ignored) {
            // Return an empty method list for malformed stored data.
        }
        return result;
    }

    public static final class Requirement {
        public final String name;
        public final double quantity;
        public final String unit;

        public Requirement(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }
}
