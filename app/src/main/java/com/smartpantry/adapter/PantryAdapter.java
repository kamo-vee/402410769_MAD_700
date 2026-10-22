package com.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.R;
import com.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ItemViewHolder> {
    public interface Actions {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final List<PantryItem> allItems = new ArrayList<>();
    private final Actions actions;
    private String filter = "";
    private static final int[] SYMBOL_COLORS = {
            R.color.butter, R.color.sage_tint, R.color.peach, R.color.blue_tint, R.color.sand_tint
    };

    public PantryAdapter(Actions actions) {
        this.actions = actions;
    }

    public void replaceItems(List<PantryItem> newItems) {
        allItems.clear();
        allItems.addAll(newItems);
        applyFilter();
    }

    public void filter(String value) {
        filter = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        applyFilter();
    }

    private void applyFilter() {
        items.clear();
        for (PantryItem item : allItems) {
            if (filter.isEmpty() || item.name.toLowerCase(Locale.ROOT).contains(filter)) {
                items.add(item);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_pantry_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.symbol.setText(item.name.isEmpty() ? "?" :
                item.name.substring(0, 1).toUpperCase(Locale.ROOT));
        holder.symbol.setBackgroundTintList(
                holder.itemView.getContext().getColorStateList(SYMBOL_COLORS[position % SYMBOL_COLORS.length]));
        holder.name.setText(item.name);
        holder.amount.setText(String.format(Locale.getDefault(), "%s %s", format(item.quantity), item.unit));
        holder.expiry.setText(item.expiryDate.isEmpty() ? "In the cupboard" : "Best by " + item.expiryDate);
        holder.edit.setOnClickListener(v -> actions.onEdit(item));
        holder.delete.setOnClickListener(v -> actions.onDelete(item));
    }

    private String format(double value) {
        return value == Math.rint(value) ? String.format(Locale.getDefault(), "%.0f", value)
                : String.format(Locale.getDefault(), "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        final TextView symbol;
        final TextView name;
        final TextView amount;
        final TextView expiry;
        final Button edit;
        final Button delete;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.item_symbol);
            name = itemView.findViewById(R.id.item_name);
            amount = itemView.findViewById(R.id.item_amount);
            expiry = itemView.findViewById(R.id.item_expiry);
            edit = itemView.findViewById(R.id.edit_item);
            delete = itemView.findViewById(R.id.delete_item);
        }
    }
}
