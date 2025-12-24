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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemsConfig {
    private static Plugin plugin;
    private static File file;
    private static FileConfiguration config;

    public void loadItemsYaml(Plugin main) {
        plugin = main;
        file = new File(main.getDataFolder(), "items.yml");
        if (!file.exists()) {
            main.saveResource("items.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void addItem(ItemStack item, double min, double max, boolean limited) {
        String name = item.getType().name();
        String path = limited ? "limited" : "unlimited";
        config.set(path + ".items." + name + ".min-price", min);
        config.set(path + ".items." + name + ".max-price", max);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Error when saving the config: " + e.getMessage());
        }
    }

    public static void reloadConfig() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Failed to upload items.yml!");
        }

    }

    public static List<String> getUnlimItems() {
        ConfigurationSection section = config.getConfigurationSection("unlimited.items");
        if (section == null) {
            section = config.createSection("unlimited.items");
        };
        return new ArrayList<>(section.getKeys(false));
    }

    public static List<String> getLimItems() {
        ConfigurationSection section = config.getConfigurationSection("limited.items");
        if (section == null) {
            section = config.createSection("limited.items");
        };
        return new ArrayList<>(section.getKeys(false));
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static int getLimitedUpdateSeconds() {
        return config.getInt("limited.update", 21600);
    }

    public static boolean isLimitedEnable() {
        return config.getBoolean("limited.enable", true);
    }

    public static int getUnlimitedUpdateSeconds() {
        return config.getInt("unlimited.update", 14400);
    }

    public static boolean isUnlimitedEnable() {
        return config.getBoolean("unlimited.enable", true);
    }
}
