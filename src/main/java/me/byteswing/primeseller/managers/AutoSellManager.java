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

package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.Items;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.configurations.database.SellItem;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Eco;
import me.byteswing.primeseller.util.Understating;
import me.byteswing.primeseller.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class AutoSellManager {
    private static PrimeSeller plugin;
    private static final Map<UUID, Boolean> autoSellEnabled = new HashMap<>();
    private static final Map<UUID, Set<Material>> autoSellMaterials = new HashMap<>();
    private static final DecimalFormat format = new DecimalFormat("##.##");

    private static File dataFile;
    private static YamlConfiguration dataConfig;

    public static void init(PrimeSeller plugin) {
        AutoSellManager.plugin = plugin;
        setupDataFile();
    }

    private static void setupDataFile() {
        try {
            dataFile = new File(plugin.getDataFolder(), "autosell-data.yml");
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                plugin.getLogger().info("Создан новый файл autosell-data.yml");
            }
            dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            plugin.getLogger().info("Файл данных автопродажи загружен");
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось создать файл данных автопродажи: " + e.getMessage());
        }
    }

    private static void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить файл данных автопродажи: " + e.getMessage());
        }
    }

    public static boolean isAutoSellEnabled(Player player) {
        return autoSellEnabled.getOrDefault(player.getUniqueId(), false);
    }

    public static void setAutoSellEnabled(Player player, boolean enabled) {
        autoSellEnabled.put(player.getUniqueId(), enabled);
    }

    public static void toggleAutoSell(Player player) {
        boolean current = isAutoSellEnabled(player);
        setAutoSellEnabled(player, !current);
    }

    public static Set<Material> getAutoSellMaterials(Player player) {
        return autoSellMaterials.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
    }

    public static boolean addAutoSellMaterial(Player player, Material material) {
        Set<Material> materials = getAutoSellMaterials(player);

        if (!hasBypassPermission(player) && materials.size() >= getMaxAutoSellSlots(player)) {
            return false;
        }

        return materials.add(material);
    }

    public static boolean removeAutoSellMaterial(Player player, Material material) {
        Set<Material> materials = getAutoSellMaterials(player);
        return materials.remove(material);
    }

    public static int getMaxAutoSellSlots(Player player) {
        if (hasBypassPermission(player)) {
            return Integer.MAX_VALUE;
        }

        int maxSlots = 0;
        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String permission = permInfo.getPermission().toLowerCase();

            if (permission.startsWith("primeseller.autosell.")) {
                try {
                    String numberStr = permission.substring("primeseller.autosell.".length());
                    int slots = Integer.parseInt(numberStr);

                    if (slots > maxSlots) {
                        maxSlots = slots;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return maxSlots;
    }

    public static boolean hasBypassPermission(Player player) {
        return player.hasPermission("primeseller.autosell.bypass");
    }

    public static void savePlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        String playerPath = "players." + playerId;

        dataConfig.set(playerPath + ".enabled", isAutoSellEnabled(player));

        Set<Material> materials = getAutoSellMaterials(player);
        List<String> materialNames = new ArrayList<>();
        for (Material material : materials) {
            materialNames.add(material.name());
        }
        dataConfig.set(playerPath + ".materials", materialNames);

        saveDataFile();
    }

    public static void clearPlayerCache(Player player) {
        autoSellEnabled.remove(player.getUniqueId());
        autoSellMaterials.remove(player.getUniqueId());
    }

    public static void loadPlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        String playerPath = "players." + playerId;

        if (dataConfig.contains(playerPath)) {
            boolean enabled = dataConfig.getBoolean(playerPath + ".enabled", false);
            autoSellEnabled.put(playerId, enabled);

            List<String> materialNames = dataConfig.getStringList(playerPath + ".materials");
            Set<Material> materials = new HashSet<>();
            for (String materialName : materialNames) {
                try {
                    Material material = Material.valueOf(materialName);
                    materials.add(material);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Неизвестный материал при загрузке данных игрока " + player.getName() + ": " + materialName);
                }
            }
            autoSellMaterials.put(playerId, materials);
        }
    }

    public static void processPlayerAutoSell(Player player) {
        if (!isAutoSellEnabled(player)) {
            return;
        }

        Set<Material> materials = getAutoSellMaterials(player);
        if (materials.isEmpty()) {
            return;
        }

        for (Material material : materials) {
            processMaterialAutoSell(player, material);
        }
    }

    private static void processMaterialAutoSell(Player player, Material material) {
        MapBase sql = new MapBase();
        for (Map.Entry<Integer, SellItem> entry : MapBase.database.entrySet()) {
            SellItem sellItem = entry.getValue();
            if (sellItem.getItem().getType() == material) {
                int slot = entry.getKey();

                int totalCount = 0;
                List<ItemStack> itemsToRemove = new ArrayList<>();

                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == material) {
                        totalCount += item.getAmount();
                        itemsToRemove.add(item);
                    }
                }

                if (totalCount <= 0) {
                    return;
                }

                int count = totalCount;

                if (sql.isLimited(slot)) {
                    int selledItems = Util.playerSellItems.getOrDefault(player.getUniqueId(), 0);
                    int itemLimit = sellItem.getPlayerItemLimit(player);
                    int totalLimit = Items.getConfig().getInt("limited.limit");
                    int itemLimitPerItems = Items.getConfig().getInt("limited.limit-per-items");

                    int availableToSell = Math.min(totalLimit - selledItems, itemLimitPerItems - itemLimit);

                    if (count > availableToSell) {
                        count = availableToSell;
                    }

                    if (count <= 0) {
                        return;
                    }

                    Util.playerSellItems.put(player.getUniqueId(), selledItems + count);
                    sellItem.addItemLimit(player, count);
                }

                double price = Double.parseDouble(format.format(sql.getPrice(slot) * count).replace(",", "."));
                Understating.takePrice(slot, count);

                int remaining = count;
                for (ItemStack item : itemsToRemove) {
                    if (remaining <= 0) break;

                    if (item.getAmount() <= remaining) {
                        remaining -= item.getAmount();
                        player.getInventory().removeItem(item);
                    } else {
                        item.setAmount(item.getAmount() - remaining);
                        remaining = 0;
                    }
                }

                Eco.addBalance(player, price);

                String itemName = LanguageManager.translate(material);
                Chat.sendMessage(player, Config.getMessage("autosell.sell")
                        .replace("%item%", itemName)
                        .replace("%price%", String.valueOf(price))
                        .replace("%amount%", "x" + count));
                break;
            }
        }
    }
}
