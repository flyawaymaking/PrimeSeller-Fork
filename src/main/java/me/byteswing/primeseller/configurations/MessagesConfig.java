package me.byteswing.primeseller.configurations;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MessagesConfig {
    private static Plugin plugin;
    private static File file;
    private static FileConfiguration config;

    public void loadConfig(@NotNull Plugin plugin) {
        MessagesConfig.plugin = plugin;
        file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reloadConfig() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().warning("Failed to upload messages.yml!");
        }
    }

    public static @NotNull String getMessage(@NotNull String key) {
        return config.getString("messages." + key, "<red>message-" + key + ": not found");
    }

    public static @NotNull List<String> getMessageList(@NotNull String key) {
        return config.getStringList("messages." + key);
    }
}
