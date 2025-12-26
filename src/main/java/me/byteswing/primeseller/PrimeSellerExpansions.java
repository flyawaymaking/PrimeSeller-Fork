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

package me.byteswing.primeseller;

import me.byteswing.primeseller.configurations.database.UnlimSoldItems;
import me.byteswing.primeseller.managers.AutoSellerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import me.byteswing.primeseller.util.Updater;
import me.byteswing.primeseller.util.Util;

public class PrimeSellerExpansions extends PlaceholderExpansion {
    private final PrimeSeller plugin;

    public PrimeSellerExpansions(PrimeSeller plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "primeseller";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getPluginMeta().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("unlimited_time_formatted")) {
            return Util.unlimitedFormat;
        }
        if (params.equalsIgnoreCase("unlimited_time")) {
            return Updater.getUnLimitedTime();
        }
        if (params.equalsIgnoreCase("limited_time_formatted")) {
            return Util.limitedFormat;
        }
        if (params.equalsIgnoreCase("limited_time")) {
            return Updater.getLimitedTime();
        }
        if (params.equalsIgnoreCase("sold_items_number")) {
            return String.valueOf(UnlimSoldItems.get(player.getUniqueId()));
        }
        if (params.equalsIgnoreCase("autoseller_status")) {
            Player onlinePlayer = player.getPlayer();
            if (onlinePlayer == null) {
                String playerName = player.getName();
                return (playerName != null ? playerName : player.getUniqueId()) + " - offline";
            }
            if (AutoSellerManager.isAutoSellEnabled(onlinePlayer)) {
                return plugin.getConfig().getString("placeholders.autosell-enabled", "enabled");
            }
            return plugin.getConfig().getString("placeholders.autosell-disabled", "disabled");
        }

        return null;
    }
}
