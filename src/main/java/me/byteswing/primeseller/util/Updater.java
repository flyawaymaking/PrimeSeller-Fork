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

package me.byteswing.primeseller.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.Items;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.menu.SellerMenu;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {
    private static final HashMap<String, Integer> counter = new HashMap<>();

    private static final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    public static void startCountdown() {
        if (counter.get("limited") == null || counter.get("unlimited") == null) {
            counter.put("unlimited", Items.getConfig().getInt("unlimited.update"));
            counter.put("limited", Items.getConfig().getInt("limited.update"));
        }
        timer.scheduleAtFixedRate(() -> {
            int lim = counter.get("limited");
            int unlim = counter.get("unlimited");
            if (lim > 1) {
                counter.put("limited", lim - 1);
            }
            if (unlim > 1) {
                counter.put("unlimited", unlim - 1);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static String getLimitedTime(int type) {
        if (type == 1) {
            return counter.get("limited") + "сек.";
        } else {
            if (type == 2) {
                int seconds = counter.get("limited");
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int remainingSeconds = seconds % 60;

                return Objects.requireNonNull(Config.getConfig().getString("time-format"))
                        .replace("hh", String.valueOf(hours))
                        .replace("mm", String.valueOf(minutes))
                        .replace("ss", String.valueOf(remainingSeconds));
            }
        }
        return "0";
    }

    public static int getLimitedTime() {
        return counter.get("limited");
    }

    public static int getUnLimitedTime() {
        return counter.get("unlimited");
    }

    public static String getUnLimitedTime(int type) {
        if (type == 1) {
            return counter.get("unlimited") + "сек.";
        } else {
            if (type == 2) {
                int seconds = counter.get("unlimited");
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int remainingSeconds = seconds % 60;

                return Objects.requireNonNull(Config.getConfig().getString("time-format"))
                        .replace("hh", String.valueOf(hours))
                        .replace("mm", String.valueOf(minutes))
                        .replace("ss", String.valueOf(remainingSeconds));
            }
        }
        return "0";
    }

    public static void update() {
        MapBase sql = new MapBase();
        sql.clear();
        Chat.broadcast(Config.getConfig().getStringList("messages.update-cast"));

        Util.update = true;
        Util.playerSellItems.clear();

        counter.put("unlimited", Items.getConfig().getInt("unlimited.update"));
        counter.put("limited", Items.getConfig().getInt("limited.update"));
        for (Player p : Bukkit.getOnlinePlayers()) {
            Util.playerSellItems.put(p.getUniqueId(), 0);
        }
        Util.unlimitedFormat = Util.formattedTime(Items.getConfig().getInt("unlimited.update"));
        Util.limitedFormat = Util.formattedTime(Items.getConfig().getInt("limited.update"));
        SellerMenu.createUnLimItems();
        SellerMenu.createLimItems();
        Understating.resetCounters();
    }

    public static void clearAndCreateLimited() {
        try {
            counter.put("limited", Items.getConfig().getInt("limited.update"));
            Util.update = true;
            Util.playerSellItems.clear();
            for (Player p : Bukkit.getOnlinePlayers()) {
                Util.playerSellItems.put(p.getUniqueId(), 0);
            }
            MapBase sql = new MapBase();
            sql.clearLimited();
            Util.limitedFormat = Util.formattedTime(Items.getConfig().getInt("limited.update"));
            SellerMenu.createLimItems();
            Chat.broadcast(Config.getConfig().getStringList("messages.limited-update-cast"));
        } catch (Exception e) {
            clearAndCreateLimited();
        }
    }

    public static void clearAndCreateUnLimited() {
        try {
            counter.put("unlimited", Items.getConfig().getInt("unlimited.update"));
            Util.update = true;
            MapBase sql = new MapBase();
            sql.clearUnLimited();
            Util.unlimitedFormat = Util.formattedTime(Items.getConfig().getInt("unlimited.update"));
            SellerMenu.createUnLimItems();
            Chat.broadcast(Config.getConfig().getStringList("messages.unlimited-update-cast"));
        } catch (Exception e) {
            clearAndCreateLimited();
        }
    }


    public static void startUnLim(PrimeSeller plugin) {
        plugin.getPluginScheduler().runTaskTimer(plugin, Updater::clearAndCreateUnLimited, 0, Items.getConfig().getInt("unlimited.update") * 20L);
    }

    public static void startLim(PrimeSeller plugin) {
        plugin.getPluginScheduler().runTaskTimer(plugin, Updater::clearAndCreateLimited, 0, Items.getConfig().getInt("limited.update") * 20L);
    }

    public static void start(PrimeSeller plugin) {
        if (Items.getConfig().getBoolean("unlimited.enable", true)) {
            startUnLim(plugin);
        }
        if (Items.getConfig().getBoolean("limited.enable", true)) {
            startLim(plugin);
        }
        Util.update = true;
    }
}
