/**
 * Copyright 2025 destroydevs (https://github.com/destroydevs/primeseller)
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

// This file was modified by flyawaymaking (https://github.com/flyawaymaking) from the original version.

package me.byteswing.primeseller.listeners;

import me.byteswing.primeseller.configurations.MessagesConfig;
import me.byteswing.primeseller.managers.EconomyManager;
import me.byteswing.primeseller.managers.LanguageManager;
import me.byteswing.primeseller.managers.SellerManager;
import me.byteswing.primeseller.menu.SellerInventoryHolder;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.MainConfig;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.configurations.database.SellItem;
import me.byteswing.primeseller.menu.SellerMenu;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.MenuHelper;
import me.byteswing.primeseller.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SellerListener implements Listener {
    private static MenuHelper menuHelper;

    public SellerListener(@NotNull PrimeSeller plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        menuHelper = plugin.getSellerMenuHelper();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (SellerInventoryHolder.isSellerInventory(event.getView().getTopInventory())) {
            event.setCancelled(true);

            Inventory clickedInv = event.getClickedInventory();
            if (!SellerInventoryHolder.isSellerInventory(clickedInv)) return;

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null) return;

            List<String> actions = menuHelper.getItemActions(clickedItem);
            if (actions == null || actions.isEmpty()) return;

            for (String action : actions) {
                handleAction(clickedInv, player, action, event.getClick());
            }
        }
    }

    private void handleAction(@NotNull Inventory clickedInv, @NotNull Player player, @NotNull String action, @NotNull ClickType clickType) {
        int idx = action.indexOf(']');
        if (idx == -1) return;
        String subAction = action.substring(idx + 1).trim();

        if (action.startsWith("[close]")) {
            player.closeInventory();
        } else if (action.startsWith("[cmd]")) {
            player.performCommand(subAction);
        } else if (action.startsWith("[update]")) {
            SellerMenu.update(player, clickedInv);
        } else if (action.startsWith("[sell-all]")) {
            sellAllItems(player);
        } else if (action.startsWith("[main-item]")) {
            int itemSlot;
            try {
                itemSlot = Integer.parseInt(subAction);
            } catch (NumberFormatException e) {
                return;
            }
            int count = 0;
            if (clickType == ClickType.LEFT) {
                count = 1;
            } else if (clickType == ClickType.RIGHT) {
                count = 64;
            } else if (clickType == ClickType.SHIFT_LEFT) {
                count = Util.getMaterialAmount(player, MapBase.get(itemSlot).getMaterial());
            }
            sellAction(clickedInv, player, itemSlot, count);
        }
    }

    private void sellAction(@NotNull Inventory inventory, @NotNull Player player, int itemSlot, int count) {
        SellItem sellItem = MapBase.get(itemSlot);
        if (sellItem == null) return;

        if (count <= 0) {
            Chat.sendMessage(player, MessagesConfig.getMessage("amount"));
            return;
        }

        Material material = sellItem.getMaterial();
        ItemStack item = ItemStack.of(material, count);
        if (!player.getInventory().containsAtLeast(item, count)) {
            Chat.sendMessage(player, MessagesConfig.getMessage("amount"));
            return;
        }

        double price;
        SellerManager.SoldData soldData;
        if (sellItem.isLimited()) {
            soldData = SellerManager.sellLimItem(player, sellItem, count);
            if (soldData.amount == 0) {
                Chat.sendMessage(player, MessagesConfig.getMessage("limit"));
                return;
            }
        } else {
            soldData = SellerManager.sellUnlimItem(player, sellItem, count);

        }
        price = soldData.price;

        Chat.sendMessage(player, MessagesConfig.getMessage("sell")
                .replace("%item%", LanguageManager.translate(material))
                .replace("%price%", EconomyManager.format(price))
                .replace("%amount%", "x" + soldData.amount));
        EconomyManager.addBalance(player, price);
        SellerMenu.update(player, inventory);
    }

    private void sellAllItems(@NotNull Player player) {
        double price = 0;
        int amount = 0;

        String type = MainConfig.getSellPriority();
        Map<Material, Integer> inventoryItems = Util.getMaterialsAmount(player);

        if (type.equals("LIMITED")) {
            SellerManager.SoldData lim = sellAllLimited(player, inventoryItems);
            SellerManager.SoldData unlim = sellAllUnLimited(player, inventoryItems);
            amount += lim.amount + unlim.amount;
            price += lim.price + unlim.price;
        }
        if (type.equals("UNLIMITED")) {
            SellerManager.SoldData unlim = sellAllUnLimited(player, inventoryItems);
            SellerManager.SoldData lim = sellAllLimited(player, inventoryItems);
            amount += lim.amount + unlim.amount;
            price += lim.price + unlim.price;
        }

        EconomyManager.addBalance(player, price);
        Chat.sendMessage(player, MessagesConfig.getMessage("sell-inventory")
                .replace("%price%", EconomyManager.format(price))
                .replace("%amount%", "x" + amount));
    }

    private @NotNull SellerManager.SoldData sellAllUnLimited(@NotNull Player player, @NotNull Map<Material, Integer> inventoryItems) {
        double price = 0;
        int amount = 0;
        for (SellItem sellItem : MapBase.database.values()) {
            if (!sellItem.isLimited()) {
                int count = inventoryItems.getOrDefault(sellItem.getMaterial(), 0);
                if (count > 0) {
                    SellerManager.SoldData soldData = SellerManager.sellUnlimItem(player, sellItem, count);
                    price += soldData.price;
                    amount += soldData.amount;
                }
            }
        }
        return new SellerManager.SoldData(price, amount);
    }

    private @NotNull SellerManager.SoldData sellAllLimited(@NotNull Player player, @NotNull Map<Material, Integer> inventoryItems) {
        double price = 0;
        int amount = 0;
        for (SellItem sellItem : MapBase.database.values()) {
            if (sellItem.isLimited()) {
                int count = inventoryItems.getOrDefault(sellItem.getMaterial(), 0);
                if (count > 0) {
                    SellerManager.SoldData soldData = SellerManager.sellLimItem(player, sellItem, count);
                    price += soldData.price;
                    amount += soldData.amount;
                }
            }
        }
        return new SellerManager.SoldData(price, amount);
    }
}
