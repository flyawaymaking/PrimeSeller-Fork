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

package me.byteswing.primeseller.menu;

import me.byteswing.primeseller.configurations.ItemsConfig;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.configurations.database.SellItem;
import me.byteswing.primeseller.configurations.database.UnlimSoldItems;
import me.byteswing.primeseller.managers.AutoSellerManager;
import me.byteswing.primeseller.managers.EconomyManager;
import me.byteswing.primeseller.tasks.PlayerGUITask;
import me.byteswing.primeseller.util.MenuHelper;
import me.byteswing.primeseller.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.util.Util;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class SellerMenu {
    private static final HashMap<UUID, BukkitTask> tasks = new HashMap<>();
    private static MenuHelper menuHelper;

    public static void init(PrimeSeller plugin) {
        menuHelper = plugin.getSellerMenuHelper();
    }

    public static void open(@NotNull Player player, @NotNull PrimeSeller plugin) {
        UUID playerId = player.getUniqueId();
        SellerInventoryHolder holder = new SellerInventoryHolder();
        Inventory inv = Bukkit.createInventory(holder, menuHelper.getSize(), menuHelper.getTitle());
        holder.setInventory(inv);

        updateSellMenu(inv, player);

        if (!tasks.containsKey(playerId)) {
            tasks.put(playerId, new PlayerGUITask(inv, player).runTaskTimer(plugin, 0, 20));
        }
        player.openInventory(inv);
    }

    public static void update(@NotNull Player player, @NotNull Inventory inv) {
        updateSellMenu(inv, player);
        player.updateInventory();
    }

    public static void deleteTask(@NotNull UUID playerId) {
        BukkitTask task = tasks.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }

    public static void disable() {
        List<UUID> playerIds = new ArrayList<>(tasks.keySet());
        for (UUID playerId : playerIds) {
            deleteTask(playerId);
        }
    }

    public static void updateSellMenu(@NotNull Inventory inv, @NotNull Player player) {
        UUID playerId = player.getUniqueId();
        for (SellItem sellItem : MapBase.database.values()) {
            Material material = sellItem.getMaterial();
            double price = sellItem.getPrice();
            String[] placeholders = {
                    "%price-x1%", EconomyManager.format(price),
                    "%price-x64%", EconomyManager.format(price * 64),
                    "%price-all%", EconomyManager.format(Util.getMaterialAmount(player, material) * price)
            };
            boolean isLimited = sellItem.isLimited();
            String path = isLimited ? "lim-item" : "unlim-item";
            if (isLimited) {
                String[] additional = {
                        "%sell%", String.valueOf(UnlimSoldItems.get(playerId)),
                        "%max%", String.valueOf(ItemsConfig.getConfig().getInt("limited.limit")),
                        "%sell-items%", String.valueOf(sellItem.getPlayerItemLimit(player.getUniqueId())),
                        "%max-items%", String.valueOf(ItemsConfig.getConfig().getInt("limited.limit-per-items"))
                };
                placeholders = Stream.concat(
                        Arrays.stream(placeholders),
                        Arrays.stream(additional)
                ).toArray(String[]::new);
            }
            menuHelper.addItemByMaterial(inv, path, material, sellItem.getSlot(), placeholders);
        }
        String[] placeholders = {
                "%lim-time%", Updater.getLimitedTime(),
                "%unlim-time%", Updater.getUnLimitedTime(),
                "%lim-time-format%", Util.limitedFormat,
                "%unlim-time-format%", Util.unlimitedFormat,
                "%autosell-slots%", String.valueOf(AutoSellerManager.getAutoSellMaterials(player).size()),
                "%autosell-max-slots%", String.valueOf(AutoSellerManager.getMaxAutoSellSlots(player))
        };
        menuHelper.addCustomItems(inv, placeholders);
        if (!menuHelper.isEnabled("divider")) return;
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) continue;

            ItemStack item = menuHelper.createCustomItem("divider");
            inv.setItem(i, item);
        }
    }
}
