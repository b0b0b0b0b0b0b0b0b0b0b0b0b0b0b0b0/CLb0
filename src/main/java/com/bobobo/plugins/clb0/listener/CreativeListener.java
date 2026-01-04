package com.bobobo.plugins.clb0.listener;

import com.bobobo.plugins.clb0.CLb;
import com.bobobo.plugins.clb0.util.CreativeLogger;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;

public class CreativeListener implements Listener {
    private final CreativeLogger creativeLogger;
    private final CLb plugin;

    public CreativeListener(CreativeLogger creativeLogger, CLb plugin) {
        this.creativeLogger = creativeLogger;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreativeClick(InventoryCreativeEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        ItemStack cursor = event.getCursor();

        if (cursor == null || cursor.getType() == org.bukkit.Material.AIR) {
            return;
        }

        if (creativeLogger.isDebug()) {
            plugin.getLogger().info("DEBUG [CreativeListener] Event triggered - Player: " + player.getName() + 
                " | Cursor: " + cursor.getType() + 
                " | Amount: " + cursor.getAmount() +
                " | RawSlot: " + event.getRawSlot() +
                " | TopSize: " + event.getView().getTopInventory().getSize() +
                " | Current: " + (event.getCurrentItem() != null ? event.getCurrentItem().getType() : "null"));
        }

        creativeLogger.logCreativeItem(player, cursor.clone());
    }
}
