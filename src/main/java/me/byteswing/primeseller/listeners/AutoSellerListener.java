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
import me.byteswing.primeseller.configurations.MessagesConfig;
import me.byteswing.primeseller.managers.AutoSellerManager;
import me.byteswing.primeseller.managers.LanguageManager;
import me.byteswing.primeseller.menu.AutoSellerInventoryHolder;
import me.byteswing.primeseller.menu.AutoSellerMenu;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.MenuHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoSellerListener implements Listener {
    private static MenuHelper menuHelper;

    public AutoSellerListener(@NotNull PrimeSeller plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        menuHelper = plugin.getAutoSellerMenuHelper();
        initOnlinePlayers();
    }

    public void initOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            AutoSellerManager.loadPlayerData(player);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AutoSellerManager.loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AutoSellerManager.savePlayerData(player);
        AutoSellerManager.clearPlayerCache(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (AutoSellerInventoryHolder.isAutoSellInventory(topInventory)) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) return;

            Player player = (Player) event.getWhoClicked();
            Inventory clickedInv = event.getClickedInventory();

            if (clickedInv == player.getInventory()) {
                addItemToAutoSell(player, clickedItem, topInventory);
                return;
            }

            if (clickedInv != topInventory) return;

            List<String> actions = menuHelper.getItemActions(clickedItem);
            if (actions == null || actions.isEmpty()) return;

            for (String action : actions) {
                handleAction(clickedInv, player, action, clickedItem);
            }
        }
    }

    private void handleAction(@NotNull Inventory clickedInv, @NotNull Player player, @NotNull String action, @NotNull ItemStack clickedItem) {
        String subAction = action.substring(action.indexOf(']') + 1).trim();
        if (action.startsWith("[close]")) {
            player.closeInventory();
        } else if (action.startsWith("[cmd]")) {
            player.performCommand(subAction);
        } else if (action.startsWith("[toggle]")) {
            AutoSellerManager.toggleAutoSell(player);
            AutoSellerManager.savePlayerData(player);
            AutoSellerMenu.updateAutoSellMenu(player, clickedInv, AutoSellerMenu.getCurrentPage(clickedInv));
        } else if (action.startsWith("[back]")) {
            int currentPage = AutoSellerMenu.getCurrentPage(clickedInv);
            AutoSellerMenu.updateAutoSellMenu(player, clickedInv, currentPage > 0 ? currentPage - 1 : 0);
        } else if (action.startsWith("[next]")) {
            int currentPage = AutoSellerMenu.getCurrentPage(clickedInv);
            AutoSellerMenu.updateAutoSellMenu(player, clickedInv, currentPage < getMaxPage(player) ? currentPage + 1 : currentPage);
        } else if (action.startsWith("[main-item]")) {
            Material material = clickedItem.getType();
            if (AutoSellerManager.removeAutoSellMaterial(player, material)) {
                AutoSellerManager.savePlayerData(player);
                Chat.sendMessage(player, MessagesConfig.getMessage("autosell.removed")
                        .replace("%item%", LanguageManager.translate(material)));
            }
            AutoSellerMenu.updateAutoSellMenu(player, clickedInv, AutoSellerMenu.getCurrentPage(clickedInv));
        }
    }

    private int getMaxPage(@NotNull Player player) {
        int playerMaterialsCount = AutoSellerManager.getAutoSellMaterials(player).size();
        int itemsSlotsCount = menuHelper.getSlots("autosell-item").size();
        return (int) Math.ceil((double) playerMaterialsCount / itemsSlotsCount);
    }

    private void addItemToAutoSell(@NotNull Player player, @NotNull ItemStack clickedItem, @NotNull Inventory topInventory) {
        if (clickedItem.getType() != Material.AIR) {
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

            AutoSellerMenu.updateAutoSellMenu(player, topInventory, AutoSellerMenu.getCurrentPage(topInventory));
        }
    }
}
