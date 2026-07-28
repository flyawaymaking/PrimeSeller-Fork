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

package me.byteswing.primeseller.economy;

import me.byteswing.primeseller.PrimeSeller;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;

import java.text.DecimalFormat;

public class ExcellentEconomy implements EconomyProvider {
    private final PrimeSeller plugin;
    private ExcellentCurrency currency;
    private ExcellentEconomyAPI api;
    private final DecimalFormat format = new DecimalFormat("##.##");

    public ExcellentEconomy(@NotNull PrimeSeller plugin) {
        this.plugin = plugin;
        String currencyName = plugin.getConfig().getString("economy.excellent-economy.currency", "money");
        RegisteredServiceProvider<ExcellentEconomyAPI> provider = Bukkit.getServer().getServicesManager().getRegistration(ExcellentEconomyAPI.class);
        if (provider != null) {
            api = provider.getProvider();
            currency = api.getCurrency(currencyName);
        }
        if (currency == null) {
            plugin.getLogger().warning("Currency '" + currencyName + "' not found in ExcellentEconomy!");
        } else {
            plugin.getLogger().info("ExcellentEconomy use '" + currencyName + "' currency!");
        }
    }

    public void addBalance(@NotNull Player player, double amount) {
        if (currency == null) {
            return;
        }

        try {
            api.deposit(player, currency, amount);
        } catch (Exception e) {
            plugin.getLogger().warning("AddBalance error: " + e.getMessage());
        }
    }

    public @NotNull String format(double amount) {
        if (currency == null) {
            return format.format(amount);
        }
        return currency.format(Double.parseDouble(format.format(amount).replace(",", ".")));
    }

    public boolean isAvailable() {
        return currency != null;
    }
}
