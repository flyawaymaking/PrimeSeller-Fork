// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.configurations.database;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SellItem {
    Material material;
    int slot;
    double price;
    HashMap<UUID, Integer> hashItemLimit = new HashMap<>();
    boolean isLimited;

    public SellItem(Material material, int slot, double price, boolean isLimited) {
        this.material = material;
        this.slot = slot;
        this.price = price;
        this.isLimited = isLimited;
    }

    public Material getMaterial() {
        return material;
    }

    public double getPrice() {
        return price;
    }

    public int getPlayerItemLimit(Player p) {
        return hashItemLimit.getOrDefault(p.getUniqueId(), 0);
    }

    public boolean isLimited() {
        return isLimited;
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
}
