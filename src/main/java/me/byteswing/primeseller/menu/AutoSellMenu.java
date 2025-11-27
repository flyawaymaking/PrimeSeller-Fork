package me.byteswing.primeseller.menu;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.managers.AutoSellManager;
import me.byteswing.primeseller.managers.LanguageManager;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Eco;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AutoSellMenu {
    private static NamespacedKey PAGE_KEY;

    public static void init(PrimeSeller plugin) {
        PAGE_KEY = new NamespacedKey(plugin, "autosell_page");
    }

    public static NamespacedKey getPageKey() {
        return PAGE_KEY;
    }

    public static void openAutoSellMenu(Player player, PrimeSeller plugin) {
        openAutoSellMenu(player, plugin, 0);
    }

    public static void openAutoSellMenu(Player player, PrimeSeller plugin, int page) {
        String title = Config.getAutoSellConfig().getString("title", "<gold>Автопродажа") + " [" + (page + 1) + "]";
        AutoSellInventoryHolder holder = new AutoSellInventoryHolder();
        Inventory inv = Bukkit.createInventory(holder, 54, Chat.toComponent(title));
        holder.setInventory(inv);
        holder.setCurrentPage(page);

        updateAutoSellMenu(player, inv, plugin, page);
        player.openInventory(inv);
    }

    public static void updateAutoSellMenu(Player player, Inventory inv, PrimeSeller plugin, int page) {
        inv.clear();

        if (inv.getHolder() instanceof AutoSellInventoryHolder holder) {
            holder.setCurrentPage(page);
        }
        createDividers(player, inv, page);
        createToggleButton(player, inv);
        createInfoButton(player, inv);
        createAutoSellSlots(player, inv, page);
        createNavigationButtons(player, inv, page);
        createBackButton(player, inv);
    }

    public static int getCurrentPage(Inventory inventory) {
        if (inventory.getHolder() instanceof AutoSellInventoryHolder holder) {
            return holder.getCurrentPage();
        }
        return 0;
    }

    private static void createDividers(Player player, Inventory inv, int page) {
        Material dividerMaterial = Material.valueOf(Config.getAutoSellConfig().getString("divider.material", "GRAY_STAINED_GLASS_PANE"));
        String dividerName = Config.getAutoSellConfig().getString("divider.name", "<white> ");
        List<String> dividerLore = Config.getAutoSellConfig().getStringList("divider.lore");

        ItemStack divider = new ItemStack(dividerMaterial);
        ItemMeta meta = divider.getItemMeta();
        meta.displayName(Chat.toComponent(dividerName));
        meta.lore(dividerLore.stream().map(Chat::toComponent).toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        divider.setItemMeta(meta);

        List<Integer> itemSlots = getItemSlotsFromConfig();

        int maxSlots = AutoSellManager.getMaxAutoSellSlots(player);

        int slotsPerPage = itemSlots.size();
        int startGlobalSlot = page * slotsPerPage;

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) continue;

            if (itemSlots.contains(i)) {
                int slotIndexInPage = itemSlots.indexOf(i);
                int globalSlotIndex = startGlobalSlot + slotIndexInPage;
                if (globalSlotIndex >= maxSlots) {
                    inv.setItem(i, divider);
                }
            } else {
                inv.setItem(i, divider);
            }
        }
    }

    private static void createNavigationButtons(Player player, Inventory inv, int page) {
        Set<Material> allMaterials = AutoSellManager.getAutoSellMaterials(player);
        List<Integer> itemSlots = getItemSlotsFromConfig();
        int totalPages = (int) Math.ceil((double) allMaterials.size() / itemSlots.size());

        if (page > 0) {
            Material prevMaterial = Material.valueOf(Config.getAutoSellConfig().getString("navigation.previous.material", "ARROW"));
            String prevName = Config.getAutoSellConfig().getString("navigation.previous.name", "<yellow>Предыдущая страница");
            List<String> prevLore = Config.getAutoSellConfig().getStringList("navigation.previous.lore");

            ItemStack prevItem = new ItemStack(prevMaterial);
            ItemMeta prevMeta = prevItem.getItemMeta();
            prevMeta.displayName(Chat.toComponent(prevName));

            List<String> formattedPrevLore = new ArrayList<>();
            for (String line : prevLore) {
                formattedPrevLore.add(line
                        .replace("%current-page%", String.valueOf(page + 1))
                        .replace("%total-pages%", String.valueOf(totalPages))
                        .replace("%total-items%", String.valueOf(allMaterials.size())));
            }
            prevMeta.lore(formattedPrevLore.stream().map(Chat::toComponent).toList());
            prevMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            prevMeta.getPersistentDataContainer().set(PAGE_KEY, PersistentDataType.INTEGER, page - 1);

            prevItem.setItemMeta(prevMeta);

            int prevSlot = Config.getAutoSellConfig().getInt("navigation.previous-slot", 45);
            inv.setItem(prevSlot, prevItem);
        }

        if (page < totalPages - 1) {
            Material nextMaterial = Material.valueOf(Config.getAutoSellConfig().getString("navigation.next.material", "ARROW"));
            String nextName = Config.getAutoSellConfig().getString("navigation.next.name", "<yellow>Следующая страница");
            List<String> nextLore = Config.getAutoSellConfig().getStringList("navigation.next.lore");

            ItemStack nextItem = new ItemStack(nextMaterial);
            ItemMeta nextMeta = nextItem.getItemMeta();
            nextMeta.displayName(Chat.toComponent(nextName));

            List<String> formattedNextLore = new ArrayList<>();
            for (String line : nextLore) {
                formattedNextLore.add(line
                        .replace("%current-page%", String.valueOf(page + 1))
                        .replace("%total-pages%", String.valueOf(totalPages))
                        .replace("%total-items%", String.valueOf(allMaterials.size())));
            }
            nextMeta.lore(formattedNextLore.stream().map(Chat::toComponent).toList());
            nextMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            nextMeta.getPersistentDataContainer().set(PAGE_KEY, PersistentDataType.INTEGER, page + 1);

            nextItem.setItemMeta(nextMeta);

            int nextSlot = Config.getAutoSellConfig().getInt("navigation.next-slot", 53);
            inv.setItem(nextSlot, nextItem);
        }
    }

    private static void createToggleButton(Player player, Inventory inv) {
        boolean enabled = AutoSellManager.isAutoSellEnabled(player);

        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String name = enabled ?
                Config.getAutoSellConfig().getString("toggle-enabled.name", "<green>Автопродажа включена") :
                Config.getAutoSellConfig().getString("toggle-disabled.name", "<red>Автопродажа выключена");

        List<String> loreConfig = enabled ?
                Config.getAutoSellConfig().getStringList("toggle-enabled.lore") :
                Config.getAutoSellConfig().getStringList("toggle-disabled.lore");

        List<String> lore = new ArrayList<>();
        for (String line : loreConfig) {
            lore.add(line
                    .replace("%slots%", String.valueOf(AutoSellManager.getAutoSellMaterials(player).size()))
                    .replace("%max-slots%", String.valueOf(AutoSellManager.getMaxAutoSellSlots(player)))
                    .replace("%status%", enabled ? "<green>включена" : "<red>выключена")
                    .replace("%player%", player.getName()));
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Chat.toComponent(name));
        meta.lore(lore.stream().map(Chat::toComponent).toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        int slot = Config.getAutoSellConfig().getInt("toggle-slot", 48);
        inv.setItem(slot, item);
    }

    private static void createInfoButton(Player player, Inventory inv) {
        Material material = Material.valueOf(Config.getAutoSellConfig().getString("info.material", "BOOK"));
        String name = Config.getAutoSellConfig().getString("info.name", "<yellow>Информация");
        List<String> loreConfig = Config.getAutoSellConfig().getStringList("info.lore");

        List<String> lore = new ArrayList<>();
        for (String line : loreConfig) {
            lore.add(line
                    .replace("%slots%", String.valueOf(AutoSellManager.getAutoSellMaterials(player).size()))
                    .replace("%max-slots%", String.valueOf(AutoSellManager.getMaxAutoSellSlots(player)))
                    .replace("%status%", AutoSellManager.isAutoSellEnabled(player) ? "<green>включена" : "<red>выключена")
                    .replace("%player%", player.getName()));
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Chat.toComponent(name));
        meta.lore(lore.stream().map(Chat::toComponent).toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        int slot = Config.getAutoSellConfig().getInt("info-slot", 50);
        inv.setItem(slot, item);
    }

    private static void createAutoSellSlots(Player player, Inventory inv, int page) {
        Set<Material> allMaterials = AutoSellManager.getAutoSellMaterials(player);
        List<Material> materialsList = new ArrayList<>(allMaterials);
        List<Integer> itemSlots = getItemSlotsFromConfig();

        int startIndex = page * itemSlots.size();
        int endIndex = Math.min(startIndex + itemSlots.size(), materialsList.size());

        for (int i = startIndex; i < endIndex; i++) {
            Material material = materialsList.get(i);
            int slotIndex = i - startIndex;

            if (slotIndex >= itemSlots.size()) break;

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            String itemName = LanguageManager.translate(material);
            meta.displayName(Chat.toComponent("<yellow>" + itemName));

            int itemsSold = AutoSellManager.getItemsSoldForMaterial(player, material);
            double moneyEarned = AutoSellManager.getMoneyEarnedForMaterial(player, material);

            List<String> loreConfig = Config.getAutoSellConfig().getStringList("item-lore");
            List<String> lore = new ArrayList<>();
            for (String line : loreConfig) {
                lore.add(line.replace("%items-sold%", String.valueOf(itemsSold))
                        .replace("%money-earned%", Eco.format(moneyEarned)));
            }

            meta.lore(lore.stream().map(Chat::toComponent).toList());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);

            inv.setItem(itemSlots.get(slotIndex), item);
        }
    }

    private static void createBackButton(Player player, Inventory inv) {
        Material material = Material.valueOf(Config.getAutoSellConfig().getString("back.material", "RED_STAINED_GLASS_PANE"));
        String name = Config.getAutoSellConfig().getString("back.name", "<red>Назад");
        List<String> lore = Config.getAutoSellConfig().getStringList("back.lore");

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Chat.toComponent(name));
        meta.lore(lore.stream().map(Chat::toComponent).toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        int slot = Config.getAutoSellConfig().getInt("back-slot", 49);
        inv.setItem(slot, item);
    }

    public static List<Integer> getItemSlotsFromConfig() {
        return Config.getAutoSellConfig().getIntegerList("item-slots");
    }
}
