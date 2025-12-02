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

package me.byteswing.primeseller.util;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.Items;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.managers.AutoSellManager;
import me.byteswing.primeseller.menu.SellerMenu;
import me.byteswing.primeseller.tasks.UpdaterTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Updater {
    private static final HashMap<String, Integer> counter = new HashMap<>();
    private static final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    private static BukkitTask unlimTask;
    private static BukkitTask limTask;

    private static void startCountdown() {
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

    public static String getLimitedTime() {
        int seconds = counter.get("limited");
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return Objects.requireNonNull(Config.getConfig().getString("time-format"))
                .replace("hh", String.valueOf(hours))
                .replace("mm", String.valueOf(minutes))
                .replace("ss", String.valueOf(remainingSeconds));
    }

    public static String getUnLimitedTime() {
        int seconds = counter.get("unlimited");
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        return Objects.requireNonNull(Config.getConfig().getString("time-format"))
                .replace("hh", String.valueOf(hours))
                .replace("mm", String.valueOf(minutes))
                .replace("ss", String.valueOf(remainingSeconds));
    }

    public static void update(PrimeSeller plugin) {
        clearAndCreateUnLimited(plugin, true);
        clearAndCreateLimited(plugin, true);
    }

    public static void clearAndCreateLimited(PrimeSeller plugin, boolean needTaskRestart) {
        try {
            if (needTaskRestart && limTask != null) {
                limTask.cancel();
            }
            counter.put("limited", Items.getConfig().getInt("limited.update"));
            Util.update = true;

            Util.playerSellItems.clear();
            for (Player p : Bukkit.getOnlinePlayers()) {
                Util.playerSellItems.put(p.getUniqueId(), 0);
            }

            MapBase sql = new MapBase();
            sql.clearLimited();

            Understating.resetCounters();
            AutoSellManager.resetAllStats();

            Util.limitedFormat = Util.formattedTime(Items.getConfig().getInt("limited.update"));

            if (!Items.getConfig().getBoolean("limited.enable", true)) return;
            SellerMenu.createLimItems();
            Chat.broadcast(Config.getConfig().getStringList("messages.limited-update-cast"));

            if (needTaskRestart) {
                long updateInterval = Items.getConfig().getInt("limited.update") * 20L;
                UpdaterTask task = new UpdaterTask(plugin, true);
                limTask = task.runTaskTimer(plugin, updateInterval, updateInterval);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error updating limited items: " + e.getMessage());
        }
    }

    public static void clearAndCreateUnLimited(PrimeSeller plugin, boolean needTaskRestart) {
        try {
            if (needTaskRestart && unlimTask != null) {
                unlimTask.cancel();
            }

            counter.put("unlimited", Items.getConfig().getInt("unlimited.update"));
            Util.update = true;

            MapBase sql = new MapBase();
            sql.clearUnLimited();

            Util.unlimitedFormat = Util.formattedTime(Items.getConfig().getInt("unlimited.update"));

            if (!Items.getConfig().getBoolean("unlimited.enable", true)) return;
            SellerMenu.createUnLimItems();
            Chat.broadcast(Config.getConfig().getStringList("messages.unlimited-update-cast"));

            if (needTaskRestart) {
                long updateInterval = Items.getConfig().getInt("unlimited.update") * 20L;
                UpdaterTask task = new UpdaterTask(plugin, false);
                unlimTask = task.runTaskTimer(plugin, updateInterval, updateInterval);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error when updating unlimited items: " + e.getMessage());
        }
    }

    public static void start(PrimeSeller plugin) {
        startCountdown();
        clearAndCreateUnLimited(plugin, true);
        clearAndCreateLimited(plugin, true);
    }

    public static void stop() {
        if (unlimTask != null) {
            unlimTask.cancel();
        }
        if (limTask != null) {
            limTask.cancel();
        }
        if (!timer.isShutdown()) {
            timer.shutdown();
        }
    }
}
