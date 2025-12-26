package me.byteswing.primeseller.configurations;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MenuConfig {
    private static Plugin plugin;
    private static File file;
    private static FileConfiguration config;

    public void loadConfig(@NotNull Plugin plugin) {
        MenuConfig.plugin = plugin;
        file = new File(plugin.getDataFolder(), "menu.yml");
        if (!file.exists()) {
            plugin.saveResource("menu.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reloadConfig() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Failed to upload menu.yml!");
        }
    }

    public static @NotNull List<Integer> getLimItemsSlots() {
        return config.getIntegerList("seller-menu.lim-item.slots");
    }

    public static @NotNull List<Integer> getUnlimItemsSlots() {
        return config.getIntegerList("seller-menu.unlim-item.slots");
    }

    public static @NotNull ConfigurationSection getConfigurationSection(@NotNull String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }
        return section;
    }
}
