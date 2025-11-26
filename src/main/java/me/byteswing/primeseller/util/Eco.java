/**
 * Copyright 2025 destroydevs (https://github.com/destroydevs/primeseller)
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

// This file was modified by flyawaymaking (https://github.com/flyawaymaking) from the original version.

package me.byteswing.primeseller.util;

import me.byteswing.primeseller.PrimeSeller;
import org.bukkit.entity.Player;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

import java.text.DecimalFormat;

public class Eco {
    private static PrimeSeller plugin;
    private static Currency currency;
    private static final DecimalFormat format = new DecimalFormat("##.##");

    public static void init(PrimeSeller plugin) {
        Eco.plugin = plugin;
        String currencyName = plugin.getConfig().getString("currency", "money");
        currency = CoinsEngineAPI.getCurrency(currencyName);
        if (currency == null) {
            plugin.getLogger().warning("Валюта '" + currencyName + "' не найдена в CoinsEngine!");
        }
    }

    public static void addBalance(Player player, double amount) {
        if (currency == null) {
            return;
        }

        try {
            CoinsEngineAPI.addBalance(player, currency, amount);
        } catch (Exception e) {
            plugin.getLogger().warning("AddBalance error: " + e.getMessage());
        }
    }

    public static String format(double amount) {
        return currency.format(Double.parseDouble(format.format(amount).replace(",", ".")))
                .replace(",", "");
    }

    public static boolean isEconomyAvailable() {
        return currency != null;
    }
}
