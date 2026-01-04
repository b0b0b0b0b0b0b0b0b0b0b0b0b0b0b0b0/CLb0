package com.bobobo.plugins.clb0.util.formatter;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogEntryFormatter {
    private final String format;
    private final SimpleDateFormat dateFormat;

    public LogEntryFormatter(String format, String dateFormatPattern) {
        this.format = format;
        this.dateFormat = new SimpleDateFormat(dateFormatPattern);
    }

    public String format(Player player, ItemStack item) {
        String date = dateFormat.format(new Date());
        String itemName = ItemNameFormatter.format(item);
        int amount = item.getAmount();

        return format
                .replace("{date}", date)
                .replace("{player}", player.getName())
                .replace("{item}", itemName)
                .replace("{amount}", String.valueOf(amount));
    }
}
