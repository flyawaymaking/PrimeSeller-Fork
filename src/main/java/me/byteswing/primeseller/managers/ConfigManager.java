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

package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.configurations.MenuConfig;
import me.byteswing.primeseller.configurations.MessagesConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import me.byteswing.primeseller.configurations.MainConfig;
import me.byteswing.primeseller.configurations.ItemsConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ConfigManager {

    private static final MainConfig config = new MainConfig();
    private static final ItemsConfig itemsConfig = new ItemsConfig();
    private static final MenuConfig menuConfig = new MenuConfig();
    private static final MessagesConfig messagesConfig = new MessagesConfig();

    public static void loadConfigurations(@NotNull Plugin plugin) {
        updateConfigFiles(plugin);

        config.loadConfig(plugin);
        itemsConfig.loadConfig(plugin);
        menuConfig.loadConfig(plugin);
        messagesConfig.loadConfig(plugin);
    }

    private static void updateConfigFiles(@NotNull Plugin plugin) {
        updateConfig(plugin, "config.yml");
        updateConfig(plugin, "menu.yml");
        updateConfig(plugin, "messages.yml");
    }

    private static void updateConfig(@NotNull Plugin plugin, @NotNull String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration defaults;

        try (InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(plugin.getResource(fileName)), StandardCharsets.UTF_8)) {
            defaults = YamlConfiguration.loadConfiguration(reader);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load default " + fileName);
            return;
        }

        boolean changed = false;

        for (String key : defaults.getKeys(true)) {
            if (defaults.isConfigurationSection(key)) {
                continue;
            }

            if (!config.contains(key)) {
                config.set(key, defaults.get(key));
                changed = true;
            }
        }

        if (changed) {
            try {
                config.save(file);
                plugin.getLogger().info("Updated " + fileName);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to save " + fileName);
            }
        }
    }

    public static void reloadConfigurations() {
        config.reloadConfig();
        itemsConfig.reloadConfig();
        menuConfig.reloadConfig();
        messagesConfig.reloadConfig();
    }
}
