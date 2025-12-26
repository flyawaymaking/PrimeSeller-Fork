// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.configurations.database;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class SellItem {
    private final int slot;
    private final Material material;
    private double price;
    private final boolean isLimited;
    private final HashMap<UUID, Integer> hashItemLimit = new HashMap<>();

    public SellItem(@NotNull Material material, int slot, double price, boolean isLimited) {
        this.slot = slot;
        this.material = material;
        this.price = price;
        this.isLimited = isLimited;
    }

    public int getSlot() {
        return slot;
    }

    public @NotNull Material getMaterial() {
        return material;
    }

    public int getPlayerItemLimit(UUID playerId) {
        return hashItemLimit.getOrDefault(playerId, 0);
    }

    public boolean isLimited() {
        return isLimited;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void addItemLimit(@NotNull UUID playerId, int count) {
        hashItemLimit.put(playerId, getPlayerItemLimit(playerId) + count);
    }
}
