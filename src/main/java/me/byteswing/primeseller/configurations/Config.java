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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;


public class Config {
    private static Plugin plugin;
    private static FileConfiguration config;

    public void loadConfig(Plugin main) {
        plugin = main;
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static ConfigurationSection getMenuConfig() {
        return config.getConfigurationSection("menu");
    }

    public static String getMessage(String key) {
        return config.getString("messages." + key, "<red>message-" + key + ": not found");
    }
}
