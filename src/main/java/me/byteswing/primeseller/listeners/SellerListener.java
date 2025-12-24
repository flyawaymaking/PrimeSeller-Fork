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
import me.byteswing.primeseller.configurations.database.UnlimSoldItems;
import me.byteswing.primeseller.managers.EconomyManager;
import me.byteswing.primeseller.managers.LanguageManager;
import me.byteswing.primeseller.menu.AutoSellerMenu;
import me.byteswing.primeseller.menu.SellerInventoryHolder;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.ItemsConfig;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.configurations.database.SellItem;
import me.byteswing.primeseller.menu.SellerMenu;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Understating;
import me.byteswing.primeseller.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SellerListener implements Listener {

    public SellerListener(PrimeSeller plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (SellerInventoryHolder.isSellerInventory(e.getView().getTopInventory())) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);

            if (e.getClickedInventory() == player.getInventory()) {
                e.setCancelled(true);
                return;
            }

            MapBase sql = new MapBase();
            ClickType clickType = e.getClick();
            player.updateInventory();

            handleSellInvSlots(e, player, sql);
            handleAutoSellInvSlots(e, player);
            handleExitSlots(e, player);
            handleCountdownSlots(e, player);

            if (clickType == ClickType.LEFT) {
                sellAction(sql, e, player, 1);
            } else if (clickType == ClickType.RIGHT) {
                sellAction(sql, e, player, 64);
            } else {
                if (clickType == ClickType.SHIFT_LEFT) {
                    sellAction(sql, e, player, Util.calc(player, sql.getSlot(e.getSlot()).getMaterial()));
                }
            }
        }
    }

    private void handleExitSlots(InventoryClickEvent e, Player player) {
        for (Integer i : Config.getMenuConfig().getIntegerList("exit.slots")) {
            if (e.getSlot() == i) {
                for (String s : Config.getMenuConfig().getStringList("exit.commands")) {
                    if (s.startsWith("[cmd]")) {
                        String cmd = s.replace("[cmd]", "").replace("[cmd] ", "");
                        player.performCommand(cmd);
                    }
                    if (s.startsWith("[close]")) {
                        player.closeInventory();
                    }
                }
                e.setCancelled(true);
                break;
            }
        }
    }

    private void handleSellInvSlots(InventoryClickEvent e, Player player, MapBase sql) {
        for (Integer i : Config.getMenuConfig().getIntegerList("sell-inventory.slots")) {
            if (e.getSlot() == i) {
                sellAllItems(sql, e, player);
                e.setCancelled(true);
                break;
            }
        }
    }

    private void handleCountdownSlots(InventoryClickEvent e, Player player) {
        for (Integer i : Config.getMenuConfig().getIntegerList("countdown.slots")) {
            if (e.getSlot() == i) {
                SellerMenu.update(player, e.getClickedInventory());
                e.setCancelled(true);
                break;
            }
        }
    }

    private void handleAutoSellInvSlots(InventoryClickEvent e, Player player) {
        if (!player.hasPermission("primeseller.autoseller")) return;
        for (Integer i : Config.getMenuConfig().getIntegerList("autosell.slots")) {
            if (e.getSlot() == i) {
                AutoSellerMenu.openAutoSellMenu(player);
                e.setCancelled(true);
                break;
            }
        }
    }

    private void sellAction(MapBase sql, InventoryClickEvent e, Player player, int count) {
        int slot = e.getSlot();
        if (MapBase.database.containsKey(slot)) {
            SellItem sellItem = sql.getSlot(slot);

            if (count <= 0) {
                Chat.sendMessage(player, MessagesConfig.getMessage("amount"));
                e.setCancelled(true);
                return;
            }

            Material material = sellItem.getMaterial();
            ItemStack item = ItemStack.of(material, count);
            if (!player.getInventory().containsAtLeast(item, count)) {
                Chat.sendMessage(player, MessagesConfig.getMessage("amount"));
                e.setCancelled(true);
                return;
            }

            if (sql.isLimited(slot)) {
                int selledItems = UnlimSoldItems.get(player.getUniqueId());
                int itemLimit = sellItem.getPlayerItemLimit(player);
                int totalLimit = ItemsConfig.getConfig().getInt("limited.limit");
                int itemLimitPerItems = ItemsConfig.getConfig().getInt("limited.limit-per-items");

                int availableToSell = Math.min(totalLimit - selledItems, itemLimitPerItems - itemLimit);

                if (count > availableToSell) {
                    count = availableToSell;
                }

                if (count <= 0) {
                    Chat.sendMessage(player, MessagesConfig.getMessage("limit"));
                    e.setCancelled(true);
                    return;
                }

                UnlimSoldItems.put(player.getUniqueId(), selledItems + count);
                sellItem.addItemLimit((Player) e.getWhoClicked(), count);
            }

            double price = sql.getPrice(slot) * count;
            Understating.takePrice(slot, count);
            Chat.sendMessage(e.getWhoClicked(), MessagesConfig.getMessage("sell")
                    .replace("%item%", LanguageManager.translate(material))
                    .replace("%price%", EconomyManager.format(price))
                    .replace("%amount%", "x" + count));
            item.setAmount(count);
            player.getInventory().removeItem(item);
            EconomyManager.addBalance(player, price);
            SellerMenu.update(player, e.getClickedInventory());
            e.setCancelled(true);
        }
    }

    private void sellAllItems(MapBase sql, InventoryClickEvent e, Player player) {
        double price = 0;
        int amount = 0;

        String type = Config.getConfig().getString("inv-sell-priority", "LIMITED");

        if (type.equals("LIMITED")) {
            Data lim = sellLimited(player, sql);
            Data unlim = sellUnLimited(player, sql);
            amount += lim.amount + unlim.amount;
            price += lim.price + unlim.price;
        }
        if (type.equals("UNLIMITED")) {
            Data unlim = sellUnLimited(player, sql);
            Data lim = sellLimited(player, sql);
            amount += lim.amount + unlim.amount;
            price += lim.price + unlim.price;
        }

        EconomyManager.addBalance(player, price);
        Chat.sendMessage(e.getWhoClicked(), MessagesConfig.getMessage("sell-inventory")
                .replace("%price%", EconomyManager.format(price))
                .replace("%amount%", "x" + amount));
    }

    private Data sellUnLimited(Player player, MapBase sql) {
        double price = 0;
        int amount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            Material itemMaterial = item.getType();
            for (Map.Entry<Integer, SellItem> d : MapBase.database.entrySet()) {
                if (!d.getValue().isLimited()) {
                    if (itemMaterial.equals(d.getValue().getMaterial())) {
                        int slot = d.getKey();
                        int count = Util.calc(player, itemMaterial);
                        if (count <= 0) {
                            continue;
                        }

                        amount += count;
                        price += sql.getPrice(slot) * count;
                        player.getInventory().removeItem(ItemStack.of(itemMaterial, count));
                        Understating.takePrice(slot, count);
                    }
                }
            }
        }
        return new Data(price, amount);
    }

    private Data sellLimited(Player player, MapBase sql) {
        double price = 0;
        int amount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            Material itemMaterial = item.getType();
            for (Map.Entry<Integer, SellItem> d : MapBase.database.entrySet()) {
                if (d.getValue().isLimited()) {
                    if (itemMaterial.equals(d.getValue().getMaterial())) {
                        int count = Util.calc(player, itemMaterial);
                        if (count <= 0) {
                            continue;
                        }
                        int slot = d.getKey();
                        SellItem sellItem = sql.getSlot(slot);

                        int selledItems = UnlimSoldItems.get(player.getUniqueId());
                        int itemLimit = sellItem.getPlayerItemLimit(player);
                        int totalLimit = ItemsConfig.getConfig().getInt("limited.limit");
                        int itemLimitPerItems = ItemsConfig.getConfig().getInt("limited.limit-per-items");

                        int availableToSell = Math.min(totalLimit - selledItems, itemLimitPerItems - itemLimit);

                        if (count > availableToSell) {
                            count = availableToSell;
                        }

                        if (count <= 0) {
                            continue;
                        }

                        UnlimSoldItems.put(player.getUniqueId(), selledItems + count);
                        sellItem.addItemLimit(player, count);

                        amount += count;
                        price += sql.getPrice(slot) * count;
                        player.getInventory().removeItem(ItemStack.of(itemMaterial, count));
                        Understating.takePrice(slot, count);
                    }
                }
            }
        }
        return new Data(price, amount);
    }

    public static class Data {
        double price;
        int amount;

        public Data(double price, int amount) {
            this.price = price;
            this.amount = amount;
        }
    }
}
