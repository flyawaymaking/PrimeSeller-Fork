/**
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

package me.byteswing.primeseller.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.MenuConfig;
import me.byteswing.primeseller.managers.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
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
    private final String menuPath;
    private final NamespacedKey actionsKey;
    private final Set<String> excludedKeys;

    public MenuHelper(PrimeSeller plugin, String menuPath, String... excludedKeys) {
        this.actionsKey = new NamespacedKey(plugin, "actions");
        this.menuPath = menuPath;
        this.excludedKeys = new HashSet<>(Arrays.asList(excludedKeys));
    }

    private ConfigurationSection getConfigSection() {
        return MenuConfig.getConfigurationSection(menuPath);
    }

    public Component getTitle(String... placeholders) {
        String title = getConfigSection().getString("title", "<red>Title");
        return Chat.toComponent(replacePlaceholders(title, placeholders));
    }

    public int getSize() {
        return getConfigSection().getInt("size", 54);
    }

    public List<Integer> getSlots(String path) {
        return getConfigSection().getIntegerList(path);
    }

    public boolean isEnabled(String path) {
        return getConfigSection().getBoolean(path, true);
    }

    public void setItemToSlots(Inventory inv, String path, ItemStack item) {
        List<Integer> slots = getConfigSection().getIntegerList(path + ".slots");
        for (int slot : slots) {
            if (slot >= 0 && slot < inv.getSize()) {
                inv.setItem(slot, item.clone());
            }
        }
    }

    public void addItemByMaterial(Inventory inventory, String path, Material material, int slot, String... placeholders) {
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }

        if (!getConfigSection().isConfigurationSection(path)) return;

        String name = "<yellow>" + LanguageManager.translate(material);
        List<Component> lore = getLore(path, placeholders);

        ItemStack item = new ItemStack(material);

        applyMetaToItem(item, name, lore, null, List.of("main-item"));

        inventory.setItem(slot, item);
    }

    public void addCustomItems(Inventory inventory,String... placeholders) {
        for (String key : getConfigSection().getKeys(false)) {
            if (excludedKeys.contains(key)) {
                continue;
            }

            String itemPath = menuPath + "." + key;

            ItemStack item = createCustomItem(itemPath, placeholders);

            setItemToSlots(inventory, itemPath, item);
        }
    }

    public ItemStack createCustomItem(String path, String... placeholders) {
        String materialName = getConfigSection().getString(path + ".material");
        Material material = null;

        if (materialName != null) {
            material = Material.getMaterial(materialName.toUpperCase());
        }

        if (material == null) {
            material = Material.STONE;
        }

        ItemStack item;

        String texture = getConfigSection().getString(path + ".texture");
        if (material == Material.PLAYER_HEAD && texture != null) {
            item = getSkull(texture);
        } else {
            item = new ItemStack(material);
        }

        String name = getConfigSection().getString(path + ".name", " ");
        List<Component> lore = getLore(path, placeholders);
        List<Double> modelDataList = getConfigSection().getDoubleList(path + ".model-data");
        List<String> actions = getConfigSection().getStringList(path + ".actions");
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
        List<String> loreStrings = getConfigSection().getStringList(path + ".lore");
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
