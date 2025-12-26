/**
 * Copyright 2025 flyawaymaking (https://github.com/flyawaymaking)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.economy.CoinsEngineEconomy;
import me.byteswing.primeseller.economy.EconomyProvider;
import me.byteswing.primeseller.economy.VaultEconomy;
import me.byteswing.primeseller.PrimeSeller;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EconomyManager {
    private static PrimeSeller plugin;
    private static EconomyProvider currentEconomy;

    public static void init(PrimeSeller plugin) {
        EconomyManager.plugin = plugin;
        reload();
    }

    public static void reload() {
        boolean isCoinsEngine = plugin.getConfig().getBoolean("economy.coins-engine.enable", false);

        if (isCoinsEngine) {
            currentEconomy = new CoinsEngineEconomy(plugin);
        } else {
            currentEconomy = new VaultEconomy(plugin);
        }
    }

    public static void addBalance(@NotNull Player player, double amount) {
        if (currentEconomy != null && currentEconomy.isAvailable()) {
            currentEconomy.addBalance(player, amount);
        } else {
            plugin.getLogger().warning("No available economy provider to add balance!");
        }
    }

    public static @NotNull String format(double amount) {
        if (currentEconomy != null && currentEconomy.isAvailable()) {
            return currentEconomy.format(amount);
        }
        return String.valueOf(amount);
    }

    public static boolean isEconomyAvailable() {
        return currentEconomy != null && currentEconomy.isAvailable();
    }
}
