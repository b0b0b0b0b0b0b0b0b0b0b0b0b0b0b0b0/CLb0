package com.bobobo.plugins.clb0.util.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class VirtualInventoryManager {
    private static final int INVENTORY_SIZE = 46;
    private static final long SYNC_TIMEOUT_MS = 2000;
    private static final long MOVE_DETECTION_TIMEOUT_MS = 800;

    private final Map<UUID, ItemStack[]> virtualInvs;
    private final Map<UUID, Long> firstPacketTime;
    private final Map<UUID, ItemStack> lastRemovedItems;
    private final Map<UUID, Long> lastRemoveTimes;
    private final Map<UUID, Integer> lastRemoveSlots;

    public VirtualInventoryManager() {
        this.virtualInvs = new IdentityHashMap<>();
        this.firstPacketTime = new IdentityHashMap<>();
        this.lastRemovedItems = new IdentityHashMap<>();
        this.lastRemoveTimes = new IdentityHashMap<>();
        this.lastRemoveSlots = new IdentityHashMap<>();
    }

    public ItemStack[] getVirtualInventory(UUID uuid) {
        return virtualInvs.computeIfAbsent(uuid, k -> new ItemStack[INVENTORY_SIZE]);
    }

    public ItemStack getItemAt(UUID uuid, int slot) {
        ItemStack[] inv = getVirtualInventory(uuid);
        if (slot < 0 || slot >= inv.length) {
            return null;
        }
        return inv[slot];
    }

    public void setItemAt(UUID uuid, int slot, ItemStack item) {
        ItemStack[] inv = getVirtualInventory(uuid);
        if (slot < 0 || slot >= inv.length) {
            return;
        }
        inv[slot] = item != null ? item.clone() : null;
    }

    public boolean isSlotEmpty(UUID uuid, int slot) {
        ItemStack item = getItemAt(uuid, slot);
        return item == null || item.getType() == Material.AIR;
    }

    public boolean isSynchronized(UUID uuid) {
        if (!firstPacketTime.containsKey(uuid)) {
            firstPacketTime.put(uuid, System.currentTimeMillis());
            return false;
        }

        long timeSinceFirst = System.currentTimeMillis() - firstPacketTime.get(uuid);
        return timeSinceFirst >= SYNC_TIMEOUT_MS;
    }

    public void handleItemRemoved(UUID uuid, int slot, ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            lastRemovedItems.put(uuid, item);
            lastRemoveTimes.put(uuid, System.currentTimeMillis());
            lastRemoveSlots.put(uuid, slot);
        }
    }

    public boolean isMove(UUID uuid, int slot, ItemStack newItem) {
        Long removeTime = lastRemoveTimes.get(uuid);
        if (removeTime == null || System.currentTimeMillis() - removeTime >= MOVE_DETECTION_TIMEOUT_MS) {
            return false;
        }

        ItemStack removed = lastRemovedItems.get(uuid);
        Integer removeSlot = lastRemoveSlots.get(uuid);

        if (removed == null || removeSlot == null) {
            return false;
        }

        if (removeSlot.equals(slot)) {
            return false;
        }

        if (removed.isSimilar(newItem) && removed.getAmount() == newItem.getAmount()) {
            lastRemoveTimes.remove(uuid);
            lastRemovedItems.remove(uuid);
            lastRemoveSlots.remove(uuid);
            return true;
        }

        return false;
    }

    public void removePlayer(UUID uuid) {
        virtualInvs.remove(uuid);
        firstPacketTime.remove(uuid);
        lastRemovedItems.remove(uuid);
        lastRemoveTimes.remove(uuid);
        lastRemoveSlots.remove(uuid);
    }
}
