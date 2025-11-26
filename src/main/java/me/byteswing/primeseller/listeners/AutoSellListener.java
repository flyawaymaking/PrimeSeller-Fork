package me.byteswing.primeseller.listeners;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.managers.AutoSellManager;
import me.byteswing.primeseller.menu.AutoSellInventoryHolder;
import me.byteswing.primeseller.menu.AutoSellMenu;
import me.byteswing.primeseller.menu.GuiMenu;
import me.byteswing.primeseller.menu.SellerInventoryHolder;
import me.byteswing.primeseller.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class AutoSellListener implements Listener {
    private final PrimeSeller plugin;

    public AutoSellListener(PrimeSeller plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initOnlinePlayers();
    }

    public void initOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            AutoSellManager.loadPlayerData(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (AutoSellInventoryHolder.isAutoSellInventory(e.getView().getTopInventory())) {
            handleAutoSellInventoryClick(e);
        } else if (SellerInventoryHolder.isSellerInventory(e.getView().getTopInventory())) {
            handleMainInventoryClick(e);
        }
    }

    private void handleAutoSellInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        int currentPage = AutoSellMenu.getCurrentPage(e.getView().getTopInventory());

        if (e.getClickedInventory() == player.getInventory()) {
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                Material material = clickedItem.getType();

                if (AutoSellManager.addAutoSellMaterial(player, material)) {
                    AutoSellManager.savePlayerData(player);
                    Chat.sendMessage(player, Config.getMessage("autosell.added")
                            .replace("%item%", material.name()));
                } else {
                    if (AutoSellManager.getAutoSellMaterials(player).size() >= AutoSellManager.getMaxAutoSellSlots(player)) {
                        Chat.sendMessage(player, Config.getMessage("autosell.limit-reached")
                                .replace("%max-slots%", String.valueOf(AutoSellManager.getMaxAutoSellSlots(player))));
                    } else {
                        Chat.sendMessage(player, Config.getMessage("autosell.already-added"));
                    }
                }

                AutoSellMenu.updateAutoSellMenu(player, e.getView().getTopInventory(), plugin, currentPage);
            }
            return;
        }

        int slot = e.getSlot();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem != null && clickedItem.hasItemMeta()) {
            ItemMeta meta = clickedItem.getItemMeta();
            Integer targetPage = meta.getPersistentDataContainer().get(AutoSellMenu.getPageKey(), PersistentDataType.INTEGER);

            if (targetPage != null) {
                AutoSellMenu.openAutoSellMenu(player, plugin, targetPage);
                return;
            }
        }

        if (slot == Config.getAutoSellConfig().getInt("toggle-slot", 49)) {
            AutoSellManager.toggleAutoSell(player);
            AutoSellManager.savePlayerData(player);
            AutoSellMenu.updateAutoSellMenu(player, e.getView().getTopInventory(), plugin, currentPage);
            return;
        }

        if (slot == Config.getAutoSellConfig().getInt("back-slot", 50)) {
            GuiMenu.open(player, plugin);
            return;
        }

        if (isItemSlot(slot)) {
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                Material material = clickedItem.getType();
                if (AutoSellManager.removeAutoSellMaterial(player, material)) {
                    AutoSellManager.savePlayerData(player);
                    Chat.sendMessage(player, Config.getMessage("autosell.removed")
                            .replace("%item%", material.name()));
                }
                AutoSellMenu.updateAutoSellMenu(player, e.getView().getTopInventory(), plugin, currentPage);
            }
        }
    }

    private boolean isItemSlot(int slot) {
        return AutoSellMenu.getItemSlotsFromConfig().contains(slot);
    }

    private void handleMainInventoryClick(InventoryClickEvent e) {
        List<Integer> autoSellSlots = Config.getAutoSellConfig().getIntegerList("slots");
        if (autoSellSlots.contains(e.getSlot())) {
            AutoSellMenu.openAutoSellMenu((Player) e.getWhoClicked(), plugin);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        AutoSellManager.loadPlayerData(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        AutoSellManager.savePlayerData(player);
        AutoSellManager.clearPlayerCache(player);
    }
}
