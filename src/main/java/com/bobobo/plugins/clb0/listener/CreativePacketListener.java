package com.bobobo.plugins.clb0.listener;

import com.bobobo.plugins.clb0.CLb;
import com.bobobo.plugins.clb0.util.CreativeLogger;
import com.bobobo.plugins.clb0.util.inventory.VirtualInventoryManager;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientCreativeInventoryAction;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CreativePacketListener extends PacketListenerAbstract implements Listener {
    private final CreativeLogger creativeLogger;
    private final CLb plugin;
    private final VirtualInventoryManager inventoryManager;

    public CreativePacketListener(CreativeLogger creativeLogger, CLb plugin) {
        super(PacketListenerPriority.MONITOR);
        this.creativeLogger = creativeLogger;
        this.plugin = plugin;
        this.inventoryManager = new VirtualInventoryManager();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE) {
            return;
        }

        if (event.getPacketType() == PacketType.Play.Client.CREATIVE_INVENTORY_ACTION) {
            handleCreativeInventoryAction(event, player);
        }
    }

    private void handleCreativeInventoryAction(PacketReceiveEvent event, Player player) {
        WrapperPlayClientCreativeInventoryAction packet = new WrapperPlayClientCreativeInventoryAction(event);

        int slot = packet.getSlot();
        if (slot == -1) {
            return;
        }

        com.github.retrooper.packetevents.protocol.item.ItemStack itemPE = packet.getItemStack();
        if (itemPE == null || itemPE.getType() == null) {
            return;
        }

        ItemStack newItem = SpigotConversionUtil.toBukkitItemStack(itemPE);
        if (newItem == null) {
            return;
        }

        UUID uuid = player.getUniqueId();

        if (!inventoryManager.isSynchronized(uuid)) {
            inventoryManager.setItemAt(uuid, slot, newItem);
            return;
        }

        ItemStack previous = inventoryManager.getItemAt(uuid, slot);

        if (newItem.getType() == Material.AIR) {
            if (previous != null && previous.getType() != Material.AIR) {
                inventoryManager.handleItemRemoved(uuid, slot, previous);
            }
            inventoryManager.setItemAt(uuid, slot, newItem);
            return;
        }

        if (inventoryManager.isMove(uuid, slot, newItem)) {
            inventoryManager.setItemAt(uuid, slot, newItem);
            return;
        }

        if (previous == null || previous.getType() == Material.AIR) {
            logCreativeTake(player, newItem, "creative menu");
        }

        inventoryManager.setItemAt(uuid, slot, newItem);
    }

    private void logCreativeTake(Player player, ItemStack item, String source) {
        if (creativeLogger.isDebug()) {
            plugin.getLogger().info(" ");
            plugin.getLogger().info("\u001B[36m" + player.getName() +
                " took from creative (" + source + "): " +
                item.getType() + " x" + item.getAmount() + "\u001B[0m");
            plugin.getLogger().info(" ");
        }

        creativeLogger.logCreativeItem(player, item.clone());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        inventoryManager.removePlayer(event.getPlayer().getUniqueId());
    }
}
