package com.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.R;
import com.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    public interface OnRecipeClick {
        void onClick(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClick listener;

    public RecipeAdapter(OnRecipeClick listener) {
        this.listener = listener;
    }

    public void replaceRecipes(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipeViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.name.setText(recipe.name);
        holder.description.setText(recipe.description);
        holder.meta.setText(recipe.category + "  ·  " + recipe.minutes + " min  ·  "
                + recipe.ingredients.size() + " ingredients");
        holder.itemView.setOnClickListener(view -> listener.onClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView description;
        final TextView meta;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipe_name);
            description = itemView.findViewById(R.id.recipe_description);
            meta = itemView.findViewById(R.id.recipe_meta);
        }
    }
}
