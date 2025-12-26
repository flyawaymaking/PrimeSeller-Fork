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

package me.byteswing.primeseller.configurations;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public class MainConfig {
    private static Plugin plugin;
    private static FileConfiguration config;

    public void loadConfig(Plugin main) {
        plugin = main;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public static @NotNull FileConfiguration getConfig() {
        return config;
    }

    public static @NotNull String getTimeFormat() {
        return config.getString("time-format", "hhh. mmm. sss.");
    }

    public static boolean isUnderstandingEnabled() {
        return config.getBoolean("understating-price.enable", true);
    }

    public static int getUnderstandingPriceItems() {
        return config.getInt("understating-price.items", 512);
    }

    public static double getUnderstandingPricePercent() {
        return config.getDouble("understating-price.percent", 0.1);
    }

    public static int getUnderstandingPriceMinPercent() {
        return config.getInt("understating-price.min-percent", 30);
    }

    public static @NotNull String getSellPriority() {
        return config.getString("inv-sell-priority", "LIMITED");
    }
}
