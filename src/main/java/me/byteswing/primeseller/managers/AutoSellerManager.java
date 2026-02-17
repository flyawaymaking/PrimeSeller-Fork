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
import me.byteswing.primeseller.configurations.MainConfig;
import me.byteswing.primeseller.configurations.MessagesConfig;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.configurations.database.SellItem;
import me.byteswing.primeseller.tasks.AutoSellTask;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoSellerManager {
    private static PrimeSeller plugin;
    private static final Map<UUID, Boolean> autoSellEnabled = new HashMap<>();
    private static final Map<UUID, Set<Material>> autoSellMaterials = new HashMap<>();
    private static final Map<UUID, Map<Material, ItemStats>> itemStats = new HashMap<>();
    private static BukkitTask autoSellTask;

    private static File dataFile;
    private static YamlConfiguration dataConfig;

    static final Pattern AUTOSELLER_LIMIT_PATTERN = Pattern.compile("^primeseller\\.autosell(?:er)?\\.(\\d+)$");

    public static void init(@NotNull PrimeSeller plugin) {
        AutoSellerManager.plugin = plugin;
        setupDataFile();
        startAutoSellTask(plugin);
    }

    private static void startAutoSellTask(@NotNull PrimeSeller plugin) {
        if (autoSellTask != null) {
            autoSellTask.cancel();
        }
        int interval = plugin.getConfig().getInt("autosell.check-interval", 100); // 100 тиков = 5 секунд
        autoSellTask = new AutoSellTask().runTaskTimer(plugin, interval, interval);
        plugin.getLogger().info("The auto sale task is started with an interval " + interval + " ticks");
    }

    public static void disable() {
        if (autoSellTask != null) {
            autoSellTask.cancel();
        }
    }

    private static void setupDataFile() {
        try {
            dataFile = new File(plugin.getDataFolder(), "autosell-data.yml");
            if (!dataFile.exists()) {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
                plugin.getLogger().info("A new file has been created autosell-data.yml");
            }
            dataConfig = YamlConfiguration.loadConfiguration(dataFile);
            plugin.getLogger().info("The autosell data file is uploaded");
        } catch (IOException e) {
            plugin.getLogger().severe("Couldn't create autosell data file: " + e.getMessage());
        }
    }

    private static void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Couldn't save autosell data file: " + e.getMessage());
        }
    }

    public static boolean isAutoSellEnabled(@NotNull Player player) {
        return player.hasPermission("primeseller.autoseller")
                && autoSellEnabled.getOrDefault(player.getUniqueId(), false);
    }

    public static void setAutoSellEnabled(@NotNull Player player, boolean enabled) {
        autoSellEnabled.put(player.getUniqueId(), enabled);
    }

    public static void toggleAutoSell(@NotNull Player player) {
        boolean current = isAutoSellEnabled(player);
        setAutoSellEnabled(player, !current);
    }

    public static @NotNull Set<Material> getAutoSellMaterials(@NotNull Player player) {
        return autoSellMaterials.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
    }

    public static boolean addAutoSellMaterial(@NotNull Player player, @NotNull Material material) {
        Set<Material> materials = getAutoSellMaterials(player);

        if (!hasBypassPermission(player) && materials.size() >= getMaxAutoSellSlots(player)) {
            return false;
        }

        return materials.add(material);
    }

    public static boolean removeAutoSellMaterial(@NotNull Player player, @NotNull Material material) {
        Set<Material> materials = getAutoSellMaterials(player);
        return materials.remove(material);
    }

    public static int getMaxAutoSellSlots(@NotNull Player player) {
        if (hasBypassPermission(player)) {
            return Integer.MAX_VALUE;
        }

        int maxSlots = 0;
        for (PermissionAttachmentInfo permInfo : player.getEffectivePermissions()) {
            String permission = permInfo.getPermission().toLowerCase();

            Matcher matcher = AUTOSELLER_LIMIT_PATTERN.matcher(permission);

            if (matcher.matches()) {
                int slots = Integer.parseInt(matcher.group(1));
                if (slots > maxSlots) {
                    maxSlots = slots;
                }
            }
        }

        return maxSlots;
    }

    public static boolean hasBypassPermission(@NotNull Player player) {
        return player.hasPermission("primeseller.autosell.bypass")
                || player.hasPermission("primeseller.autoseller.bypass");
    }

    public static void savePlayerData(@NotNull Player player) {
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

    public static void clearPlayerCache(@NotNull Player player) {
        autoSellEnabled.remove(player.getUniqueId());
        autoSellMaterials.remove(player.getUniqueId());
    }

    public static void loadPlayerData(@NotNull Player player) {
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
                    plugin.getLogger().warning("Unknown material when uploading player data " + player.getName() + ": " + materialName);
                }
            }
            autoSellMaterials.put(playerId, materials);
        }
    }

    public static void processPlayerAutoSell(@NotNull Player player) {
        if (!isAutoSellEnabled(player)) {
            return;
        }

        Set<Material> materials = getAutoSellMaterials(player);
        if (materials.isEmpty()) {
            return;
        }

        processAutoSellMaterials(player, materials);
    }

    private static void processAutoSellMaterials(@NotNull Player player, @NotNull Set<Material> materials) {
        double price = 0;
        int count = 0;
        Map<Material, Integer> inventoryItems = Util.getMaterialsAmount(player);

        for (SellItem sellItem : MapBase.database.values()) {
            Material sellMaterial = sellItem.getMaterial();

            if (materials.contains(sellMaterial)) {
                int totalCount = inventoryItems.getOrDefault(sellMaterial, 0);

                if (totalCount <= 0) {
                    continue;
                }

                SellerManager.SoldData soldData;
                if (sellItem.isLimited()) {
                    soldData = SellerManager.sellLimItem(player, sellItem, totalCount);
                    if (soldData.amount == 0) {
                        continue;
                    }
                } else {
                    soldData = SellerManager.sellUnlimItem(player, sellItem, totalCount);
                }
                price += soldData.price;
                count += soldData.amount;
                getItemStats(player, sellMaterial).addSale(count, price);
            }
        }
        EconomyManager.addBalance(player, price);

        if (MainConfig.getConfig().getBoolean("autosell.enable-autosell-messages", false)) {
            Chat.sendMessage(player, MessagesConfig.getMessage("autosell.sell")
                    .replace("%price%", EconomyManager.format(price))
                    .replace("%amount%", "x" + count));
        }
    }

    public static @NotNull ItemStats getItemStats(@NotNull Player player, @NotNull Material material) {
        Map<Material, ItemStats> playerStats = itemStats.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        return playerStats.computeIfAbsent(material, k -> new ItemStats());
    }

    public static int getItemsSoldForMaterial(@NotNull Player player, @NotNull Material material) {
        ItemStats stats = getItemStats(player, material);
        return stats.getItemsSold();
    }

    public static double getMoneyEarnedForMaterial(@NotNull Player player, @NotNull Material material) {
        ItemStats stats = getItemStats(player, material);
        return stats.getMoneyEarned();
    }

    public static void resetAllLimitedStats() {
        for (Map<Material, ItemStats> playerStats : itemStats.values()) {
            for (Map.Entry<Material, ItemStats> entry : playerStats.entrySet()) {
                Material material = entry.getKey();

                for (SellItem sellItem : MapBase.database.values()) {
                    if (sellItem.getMaterial() == material && sellItem.isLimited()) {
                        entry.getValue().reset();
                        break;
                    }
                }
            }
        }
    }

    public static void resetAllUnlimitedStats() {
        for (Map<Material, ItemStats> playerStats : itemStats.values()) {
            for (Map.Entry<Material, ItemStats> entry : playerStats.entrySet()) {
                Material material = entry.getKey();

                for (SellItem sellItem : MapBase.database.values()) {
                    if (sellItem.getMaterial() == material && !sellItem.isLimited()) {
                        entry.getValue().reset();
                        break;
                    }
                }
            }
        }
    }

    public static class ItemStats {
        private int itemsSold = 0;
        private double moneyEarned = 0.0;

        public void addSale(int items, double money) {
            this.itemsSold += items;
            this.moneyEarned += money;
        }

        public int getItemsSold() {
            return itemsSold;
        }

        public double getMoneyEarned() {
            return moneyEarned;
        }

        public void reset() {
            itemsSold = 0;
            moneyEarned = 0.0;
        }
    }
}
