package me.byteswing.primeseller.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.byteswing.primeseller.PrimeSeller;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MenuHelper {
    private FileConfiguration menuConfig;
    private final NamespacedKey actionsKey;

    public MenuHelper(PrimeSeller plugin, FileConfiguration menuConfig) {
        this.actionsKey = new NamespacedKey(plugin, "actions");
        this.menuConfig = menuConfig;
    }

    public void addItemsFromConfig(Inventory inventory, String path, Material material, int slot, String... placeholders) {
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }

        if (!menuConfig.isConfigurationSection(path)) return;

        List<Component> lore = getLore(path, placeholders);

        ItemStack item = new ItemStack(material);

        applyMetaToItem(item, null, lore, null, null);

        inventory.setItem(slot, item);
    }

    public void addCustomItems(Inventory inventory, String menuPath, String... placeholders) {
        ConfigurationSection menuSection = menuConfig.getConfigurationSection(menuPath);
        if (menuSection == null) return;

        for (String key : menuSection.getKeys(false)) {
            if (key.equals("title") || key.equals("size") ||
                    key.equals("lim-items") || key.equals("unlim-items") ||
                    key.equals("autosell-items") || key.equals("divider") ||
                    key.equals("toggle-button")) {
                continue;
            }

            String itemPath = menuPath + "." + key;

            ItemStack item = createCustomItem(itemPath, placeholders);

            List<Integer> slots = menuSection.getIntegerList(itemPath + ".slots");
            for (int slot : slots) {
                if (slot >= 0 && slot < inventory.getSize()) {
                    inventory.setItem(slot, item.clone());
                }
            }
        }
    }

    public ItemStack createCustomItem(String path, String... placeholders) {
        String materialName = menuConfig.getString(path + ".material");
        Material material = null;

        if (materialName != null) {
            material = Material.getMaterial(materialName.toUpperCase());
        }

        if (material == null) {
            material = Material.STONE;
        }

        ItemStack item;

        String texture = menuConfig.getString(path + ".texture");
        if (material == Material.PLAYER_HEAD && texture != null) {
            item = getSkull(texture);
        } else {
            item = new ItemStack(material);
        }

        String name = menuConfig.getString(path + ".name", " ");
        List<Component> lore = getLore(path, placeholders);
        List<Double> modelDataList = menuConfig.getDoubleList(path + ".model-data");
        List<String> actions = menuConfig.getStringList(path + ".actions");
        applyMetaToItem(item, name, lore, modelDataList, actions);
        return item;
    }

    private ItemStack getSkull(String base64Texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            try {
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
                ProfileProperty property = new ProfileProperty("textures", base64Texture);
                profile.setProperty(property);

                meta.setPlayerProfile(profile);
            } catch (Exception ignored) {
            }

            skull.setItemMeta(meta);
        }

        return skull;
    }

    private List<Component> getLore(String path, String... placeholders) {
        List<String> loreStrings = menuConfig.getStringList(path + ".lore");
        List<Component> lore = new ArrayList<>();
        for (String loreLine : loreStrings) {
            loreLine = replacePlaceholders(loreLine, placeholders);
            lore.add(Chat.toComponent(loreLine));
        }
        return lore;
    }

    private void applyMetaToItem(ItemStack item, String displayName, List<Component> lore, List<Double> modelDataList, List<String> actions) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (displayName != null) {
                meta.displayName(Chat.toComponent(displayName));
            }
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            if (modelDataList != null) {
                applyCustomModelData(meta, modelDataList);
            }
            if (actions != null) {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                String actionsString = String.join(";:;", actions);
                pdc.set(actionsKey, PersistentDataType.STRING, actionsString);
            }
            item.setItemMeta(meta);
        }
    }

    private void applyCustomModelData(ItemMeta meta, @NotNull List<Double> modelDataList) {
        if (modelDataList.isEmpty()) {
            return;
        }

        CustomModelDataComponent customData = meta.getCustomModelDataComponent();

        List<Float> floatIds = new ArrayList<>();
        for (Double value : modelDataList) {
            floatIds.add(value.floatValue());
        }

        customData.setFloats(floatIds);
        meta.setCustomModelDataComponent(customData);
    }

    private String replacePlaceholders(String text, String... placeholders) {
        if (placeholders == null || placeholders.length % 2 != 0) {
            return text;
        }

        String result = text;
        for (int i = 0; i < placeholders.length; i += 2) {
            result = result.replace(placeholders[i], placeholders[i + 1]);
        }
        return result;
    }

    public List<String> getItemActions(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        String actionsString = pdc.get(actionsKey, PersistentDataType.STRING);
        if (actionsString == null || actionsString.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.asList(actionsString.split(";:;"));
    }
}
