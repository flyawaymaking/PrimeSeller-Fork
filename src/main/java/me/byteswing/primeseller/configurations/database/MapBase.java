// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.configurations.database;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public class MapBase {

    public static final LinkedHashMap<Integer, SellItem> database = new LinkedHashMap<>();

    public static void saveMaterial(@NotNull Material material, int itemSlot, double p, boolean limited) {
        database.put(itemSlot, new SellItem(material, itemSlot, p, limited));
    }

    public static SellItem get(int itemSlot) {
        return database.get(itemSlot);
    }

    public static void clear() {
        database.clear();
    }

    public static void clearLimited() {
        database.entrySet().removeIf(entry -> entry.getValue().isLimited());
    }

    public static void clearUnLimited() {
        database.entrySet().removeIf(entry -> !entry.getValue().isLimited());
    }
}
