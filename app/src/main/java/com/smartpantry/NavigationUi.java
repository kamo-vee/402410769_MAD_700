package com.smartpantry;

import android.graphics.Typeface;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

public final class NavigationUi {
    private static final int[] TAB_IDS = {
            R.id.nav_pantry, R.id.nav_recipes, R.id.nav_settings
    };

    private NavigationUi() {}

    public static void select(AppCompatActivity activity, int selectedId) {
        for (int tabId : TAB_IDS) {
            TextView tab = activity.findViewById(tabId);
            if (tab == null) continue;
            boolean selected = tabId == selectedId;
            int color = activity.getColor(selected ? R.color.forest : R.color.muted);
            tab.setTextColor(color);
            tab.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
            tab.setBackgroundResource(selected
                    ? R.drawable.nav_tab_selected : R.drawable.nav_tab_unselected);
            android.graphics.drawable.Drawable icon = tab.getCompoundDrawables()[1];
            if (icon != null) {
                DrawableCompat.setTint(DrawableCompat.wrap(icon.mutate()), color);
            }
        }
    }
}
