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

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import me.byteswing.primeseller.util.Updater;
import me.byteswing.primeseller.util.Util;

public class Expansions extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "primeseller-fork";
    }

    @Override
    public @NotNull String getAuthor() {
        return "destroydev, golovin12";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
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
        if (params.equalsIgnoreCase("selled_items")) {
            return Util.playerSellItems.get(player.getUniqueId()).toString();
        }

        return null;
    }
}
