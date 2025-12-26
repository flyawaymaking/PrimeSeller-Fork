package me.byteswing.primeseller.configurations.database;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class UnlimSoldItems {
    private static final HashMap<UUID, Integer> playerSoldItems = new HashMap<>();

    public static void clear() {
        playerSoldItems.clear();
    }

    public static void put(@NotNull UUID playerId, int sold) {
        playerSoldItems.put(playerId, sold);
    }

    public static int get(@NotNull UUID playerId) {
        return playerSoldItems.getOrDefault(playerId, 0);
    }
}
