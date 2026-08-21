package me.byteswing.primeseller.economy;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface EconomyProvider {
    void addBalance(Player player, double amount);

    String format(double amount);

    boolean isAvailable();

    default double round(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
