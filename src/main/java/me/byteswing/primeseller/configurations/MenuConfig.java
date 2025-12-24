package me.byteswing.primeseller.configurations;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class MenuConfig {

    public static List<Integer> getLimItemsSlots() {
        return config.getIntegerList("seller-menu.lim-item.slots");
    }

    public static List<Integer> getUnlimItemsSlots() {
        return config.getIntegerList("seller-menu.unlim-item.slots");
    }

    public static ConfigurationSection getConfigurationSection(String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }
        return section;
    }
}
