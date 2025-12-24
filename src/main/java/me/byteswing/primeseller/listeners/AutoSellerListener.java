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

package me.byteswing.primeseller.listeners;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.MessagesConfig;
import me.byteswing.primeseller.managers.AutoSellerManager;
import me.byteswing.primeseller.managers.LanguageManager;
import me.byteswing.primeseller.menu.AutoSellerInventoryHolder;
import me.byteswing.primeseller.menu.AutoSellerMenu;
import me.byteswing.primeseller.menu.SellerMenu;
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

public class AutoSellerListener implements Listener {
    private final PrimeSeller plugin;

    public AutoSellerListener(PrimeSeller plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initOnlinePlayers();
    }

    public void initOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            AutoSellerManager.loadPlayerData(player);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        AutoSellerManager.loadPlayerData(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        AutoSellerManager.savePlayerData(player);
        AutoSellerManager.clearPlayerCache(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (AutoSellerInventoryHolder.isAutoSellInventory(e.getView().getTopInventory())) {
            handleAutoSellInventoryClick(e);
        }
    }

    private void handleAutoSellInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        e.setCancelled(true);

        int currentPage = AutoSellerMenu.getCurrentPage(e.getView().getTopInventory());

        if (e.getClickedInventory() == player.getInventory()) {
            ItemStack clickedItem = e.getCurrentItem();
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                Material material = clickedItem.getType();

                if (AutoSellerManager.addAutoSellMaterial(player, material)) {
                    AutoSellerManager.savePlayerData(player);
                    Chat.sendMessage(player, MessagesConfig.getMessage("autosell.added")
                            .replace("%item%", LanguageManager.translate(material)));
                } else {
                    if (AutoSellerManager.getAutoSellMaterials(player).size() >= AutoSellerManager.getMaxAutoSellSlots(player)) {
                        Chat.sendMessage(player, MessagesConfig.getMessage("autosell.limit-reached")
                                .replace("%max-slots%", String.valueOf(AutoSellerManager.getMaxAutoSellSlots(player))));
                    } else {
                        Chat.sendMessage(player, MessagesConfig.getMessage("autosell.already-added"));
                    }
                }

                AutoSellerMenu.updateAutoSellMenu(player, e.getView().getTopInventory(), currentPage);
            }
            return;
        }

        int slot = e.getSlot();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem != null && clickedItem.hasItemMeta()) {
            ItemMeta meta = clickedItem.getItemMeta();
            Integer targetPage = meta.getPersistentDataContainer().get(AutoSellerMenu.getPageKey(), PersistentDataType.INTEGER);

            if (targetPage != null) {
                AutoSellerMenu.openAutoSellMenu(player, targetPage);
                return;
            }
        }

        if (slot == Config.getAutoSellConfig().getInt("toggle-slot", 49)) {
            AutoSellerManager.toggleAutoSell(player);
            AutoSellerManager.savePlayerData(player);
            AutoSellerMenu.updateAutoSellMenu(player, e.getView().getTopInventory(), currentPage);
            return;
        }

        if (slot == Config.getAutoSellConfig().getInt("back-slot", 50)) {
            if (player.hasPermission("primeseller.seller")) {
                SellerMenu.open(player, plugin);
            }
            return;
        }

        if (isItemSlot(slot)) {
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                Material material = clickedItem.getType();
                if (AutoSellerManager.removeAutoSellMaterial(player, material)) {
                    AutoSellerManager.savePlayerData(player);
                    Chat.sendMessage(player, MessagesConfig.getMessage("autosell.removed")
                            .replace("%item%", LanguageManager.translate(material)));
                }
                AutoSellerMenu.updateAutoSellMenu(player, e.getView().getTopInventory(), currentPage);
            }
        }
    }

    private boolean isItemSlot(int slot) {
        return AutoSellerMenu.getItemSlotsFromConfig().contains(slot);
    }
}
