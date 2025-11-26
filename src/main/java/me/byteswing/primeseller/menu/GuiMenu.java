/**
 * Copyright 2025 destroydevs (https://github.com/destroydevs/primeseller)
 * Copyright 2025 flyawaymaking (https://github.com/flyawaymaking)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This file was modified by flyawaymaking (https://github.com/flyawaymaking) from the original version.

package me.byteswing.primeseller.menu;

import me.byteswing.primeseller.configurations.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Util;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class GuiMenu {
    public static final HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    public static void open(Player player, PrimeSeller plugin) {
        UUID playerId = player.getUniqueId();
        Inventory inv = Bukkit.createInventory(
                new SellerInventoryHolder(null),
                Config.getMenuConfig().getInt("size"),
                Chat.toComponent(Config.getMenuConfig().getString("title"))
        );

        if (!Util.playerSellItems.containsKey(playerId)) {
            Util.playerSellItems.put(playerId, 0);
        }

        try {
            Util.fillInventory(inv, player, plugin);
        } catch (NullPointerException e) {
            Chat.sendMessage(player, Config.getMessage("items-loading"));
            return;
        }

        if (!tasks.containsKey(playerId)) {
            tasks.put(playerId, plugin.getPluginScheduler().runTaskTimer(plugin, () -> {
                if (Util.update && tasks.containsKey(playerId)) {
                    try {
                        Util.fillInventory(inv, player, plugin);
                    } catch (NullPointerException e) {
                        return;
                    }
                    Util.update = false;
                }
            }, 0, 20));
        }
        player.openInventory(inv);
    }

    public static void update(Player player, Inventory inv, PrimeSeller plugin) {
        if (!SellerInventoryHolder.isSellerInventory(inv)) {
            return;
        }

        if (!Util.playerSellItems.containsKey(player.getUniqueId())) {
            Util.playerSellItems.put(player.getUniqueId(), 0);
        }

        try {
            Util.fillInventory(inv, player, plugin);
        } catch (NullPointerException e) {
            return;
        }
        player.updateInventory();
    }
}
