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
import org.jetbrains.annotations.Nullable;

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

    private @NotNull ConfigurationSection getConfigSection() {
        return MenuConfig.getConfigurationSection(menuPath);
    }

    public @NotNull Component getTitle(@NotNull String... placeholders) {
        String title = getConfigSection().getString("title", "<red>Title");
        return Chat.toComponent(replacePlaceholders(title, placeholders));
    }

    public int getSize() {
        return getConfigSection().getInt("size", 54);
    }

    public @NotNull List<Integer> getSlots(@NotNull String path) {
        return getConfigSection().getIntegerList(path);
    }

    public boolean isEnabled(@NotNull String path) {
        return getConfigSection().getBoolean(path, true);
    }

    public void setItemToSlots(@NotNull Inventory inv, @NotNull String path, @NotNull ItemStack item) {
        List<Integer> slots = getConfigSection().getIntegerList(path + ".slots");
        for (int slot : slots) {
            if (slot >= 0 && slot < inv.getSize()) {
                inv.setItem(slot, item.clone());
            }
        }
    }

    public void addItemByMaterial(@NotNull Inventory inventory, @NotNull String path, @NotNull Material material, int slot, @NotNull String... placeholders) {
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }

        if (!getConfigSection().isConfigurationSection(path)) return;

        String name = "<yellow>" + LanguageManager.translate(material);
        List<Component> lore = getLore(path, placeholders);

        ItemStack item = new ItemStack(material);

        applyMetaToItem(item, name, lore, null, List.of("[main-item] " + slot));

        inventory.setItem(slot, item);
    }

    public void addCustomItems(@NotNull Inventory inventory, @NotNull String... placeholders) {
        for (String key : getConfigSection().getKeys(false)) {
            if (excludedKeys.contains(key)) {
                continue;
            }

            String itemPath = menuPath + "." + key;

            ItemStack item = createCustomItem(itemPath, placeholders);

            setItemToSlots(inventory, itemPath, item);
        }
    }

    public @NotNull ItemStack createCustomItem(@NotNull String path, @NotNull String... placeholders) {
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

    private @NotNull ItemStack getSkull(@NotNull String base64Texture) {
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

    private @NotNull List<Component> getLore(@NotNull String path, @NotNull String... placeholders) {
        List<String> loreStrings = getConfigSection().getStringList(path + ".lore");
        List<Component> lore = new ArrayList<>();
        for (String loreLine : loreStrings) {
            loreLine = replacePlaceholders(loreLine, placeholders);
            lore.add(Chat.toComponent(loreLine));
        }
        return lore;
    }

    private void applyMetaToItem(@NotNull ItemStack item, @Nullable String displayName, @NotNull List<Component> lore, @Nullable List<Double> modelDataList, @Nullable List<String> actions) {
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

    private void applyCustomModelData(@NotNull ItemMeta meta, @NotNull List<Double> modelDataList) {
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

    private @NotNull String replacePlaceholders(@NotNull String text, @NotNull String... placeholders) {
        if (placeholders == null || placeholders.length % 2 != 0) {
            return text;
        }

        String result = text;
        for (int i = 0; i < placeholders.length; i += 2) {
            result = result.replace(placeholders[i], placeholders[i + 1]);
        }
        return result;
    }

    public @Nullable List<String> getItemActions(@Nullable ItemStack item) {
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
