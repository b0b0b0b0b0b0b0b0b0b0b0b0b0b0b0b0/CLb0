package com.bobobo.plugins.clb0.util.formatter;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ItemNameFormatter {
    private ItemNameFormatter() {
    }

    public static String format(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }

        Material material = item.getType();
        String name = material.name().toLowerCase().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
        }

        return result.toString();
    }
}
