// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.configurations.database;

import org.bukkit.Material;

import java.util.LinkedHashMap;

public class MapBase {

    public static final LinkedHashMap<Integer, SellItem> database = new LinkedHashMap<>();

    public static void saveMaterial(Material material, int slot, double p, boolean limited) {
        database.put(slot, new SellItem(material, slot, p, limited));
    }

    public SellItem getSlot(int slot) {
        return database.get(slot);
    }

    public double getPrice(int slot) {
        return getSlot(slot).getPrice();
    }

    public void setPrice(int slot, double p) {
        SellItem item = getSlot(slot);
        item.setPrice(p);
    }

    public void clear() {
        database.clear();
    }

    public void clearLimited() {
        database.keySet().removeIf(this::isLimited);
    }

    public void clearUnLimited() {
        database.keySet().removeIf(s -> !isLimited(s));
    }

    public boolean isLimited(int slot) {
        return getSlot(slot).isLimited();
    }
}
