package me.byteswing.primeseller.economy;

import org.bukkit.entity.Player;

public interface EconomyProvider {
    void addBalance(Player player, double amount);

    String format(double amount);

    boolean isAvailable();
}
