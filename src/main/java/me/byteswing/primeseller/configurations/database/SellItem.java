// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.configurations.database;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class SellItem implements Cloneable {
    public static int limit;
    ItemStack item;
    int slot;
    double price;
    HashMap<UUID, Integer> hashItemLimit = new HashMap<>();
    int itemLimit;
    boolean isLimited;

    public SellItem(ItemStack item, int slot, double price, int itemLimit, boolean isLimited) {
        this.item = item;
        this.slot = slot;
        this.price = price;
        this.itemLimit = itemLimit;
        this.isLimited = isLimited;
    }

    public static int getLimit() {
        return limit;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }

    public double getPrice() {
        return price;
    }

    public int getItemLimit() {
        return itemLimit;
    }

    public int getPlayerItemLimit(Player p) {
        hashItemLimit.putIfAbsent(p.getUniqueId(), 0);
        return hashItemLimit.getOrDefault(p.getUniqueId(), 0);
    }

    public boolean isLimited() {
        return isLimited;
    }

    public static void setLimit(int limit) {
        SellItem.limit = limit;
    }

    public void addItemLimit(Player p, int count) {
        setItemLimit(p, getPlayerItemLimit(p) + count);
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setItemLimit(Player p, int itemLimit) {
        hashItemLimit.put(p.getUniqueId(), itemLimit);
    }

    @Override
    public SellItem clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (SellItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
