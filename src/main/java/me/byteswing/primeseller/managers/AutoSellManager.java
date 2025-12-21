package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.Items;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.configurations.database.SellItem;
import me.byteswing.primeseller.tasks.AutoSellTask;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Understating;
import me.byteswing.primeseller.util.Util;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class AutoSellManager {
    private static PrimeSeller plugin;
    private static final Map<UUID, Boolean> autoSellEnabled = new HashMap<>();
    private static final Map<UUID, Set<Material>> autoSellMaterials = new HashMap<>();
    private static final Map<UUID, Map<Material, ItemStats>> itemStats = new HashMap<>();
    private static final DecimalFormat format = new DecimalFormat("##.##");
    private static BukkitTask autoSellTask;

    private static File dataFile;
    private static YamlConfiguration dataConfig;

    public static void init(PrimeSeller plugin) {
        AutoSellManager.plugin = plugin;
        setupDataFile();
        startAutoSellTask(plugin);
    }

    private static void startAutoSellTask(PrimeSeller plugin) {
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

    public static boolean isAutoSellEnabled(Player player) {
        return player.hasPermission("primeseller.autoseller")
                && autoSellEnabled.getOrDefault(player.getUniqueId(), false);
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
                    plugin.getLogger().warning("Unknown material when uploading player data " + player.getName() + ": " + materialName);
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
            ItemStack sellItemStack = sellItem.getItem();

            if (sellItemStack.getType() == material) {
                int slot = entry.getKey();

                int totalCount = Util.calc(player, sellItemStack);

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

                getItemStats(player, material).addSale(count, price);

                ItemStack itemToRemove = sellItemStack.clone();
                itemToRemove.setAmount(count);
                player.getInventory().removeItem(itemToRemove);

                EconomyManager.addBalance(player, price);

                if (Config.getConfig().getBoolean("autosell.enable-autosell-messages", false)) {
                    String itemName = LanguageManager.translate(material);
                    Chat.sendMessage(player, Config.getMessage("autosell.sell")
                            .replace("%item%", itemName)
                            .replace("%price%", EconomyManager.format(price))
                            .replace("%amount%", "x" + count));
                }
                break;
            }
        }
    }

    public static ItemStats getItemStats(Player player, Material material) {
        Map<Material, ItemStats> playerStats = itemStats.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        return playerStats.computeIfAbsent(material, k -> new ItemStats());
    }

    public static int getItemsSoldForMaterial(Player player, Material material) {
        ItemStats stats = getItemStats(player, material);
        return stats.getItemsSold();
    }

    public static double getMoneyEarnedForMaterial(Player player, Material material) {
        ItemStats stats = getItemStats(player, material);
        return stats.getMoneyEarned();
    }

    public static void resetAllLimitedStats() {
        MapBase mapBase = new MapBase();

        for (Map<Material, ItemStats> playerStats : itemStats.values()) {
            for (Map.Entry<Material, ItemStats> entry : playerStats.entrySet()) {
                Material material = entry.getKey();

                for (Map.Entry<Integer, SellItem> dbEntry : MapBase.database.entrySet()) {
                    SellItem sellItem = dbEntry.getValue();
                    int slot = dbEntry.getKey();

                    if (sellItem.getItem().getType() == material && mapBase.isLimited(slot)) {
                        entry.getValue().reset();
                        break;
                    }
                }
            }
        }
    }

    public static void resetAllUnlimitedStats() {
        MapBase mapBase = new MapBase();

        for (Map<Material, ItemStats> playerStats : itemStats.values()) {
            for (Map.Entry<Material, ItemStats> entry : playerStats.entrySet()) {
                Material material = entry.getKey();

                for (Map.Entry<Integer, SellItem> dbEntry : MapBase.database.entrySet()) {
                    SellItem sellItem = dbEntry.getValue();
                    int slot = dbEntry.getKey();

                    if (sellItem.getItem().getType() == material && !mapBase.isLimited(slot)) {
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
