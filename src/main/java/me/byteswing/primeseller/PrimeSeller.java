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

package me.byteswing.primeseller;

import me.byteswing.primeseller.managers.*;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Eco;
import me.byteswing.primeseller.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class PrimeSeller extends JavaPlugin {

    public BukkitScheduler getPluginScheduler() {
        return this.getServer().getScheduler();
    }

    @Override
    public void onEnable() {
        ConfigManager.loadConfigurations(this);
        saveDefaultConfig();
        Eco.init(this);
        if (!Eco.isEconomyAvailable()) {
            getLogger().severe("Плагин выключен, причина - в CoinsEngine не найдена валюта из конфига");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Chat.init(this);
        loadManager(new ListenerManager(), this);
        loadManager(new CommandManager(), this);
        Updater.startCountdown();
        Updater.start(this);
        msg("██████╗░██████╗░██╗███╗░░░███╗███████╗░██████╗███████╗██╗░░░░░██╗░░░░░███████╗██████╗░");
        msg("██╔══██╗██╔══██╗██║████╗░████║██╔════╝██╔════╝██╔════╝██║░░░░░██║░░░░░██╔════╝██╔══██╗");
        msg("██████╔╝██████╔╝██║██╔████╔██║█████╗░░╚█████╗░█████╗░░██║░░░░░██║░░░░░█████╗░░██████╔╝");
        msg("██╔═══╝░██╔══██╗██║██║╚██╔╝██║██╔══╝░░░╚═══██╗██╔══╝░░██║░░░░░██║░░░░░██╔══╝░░██╔══██╗");
        msg("██║░░░░░██║░░██║██║██║░╚═╝░██║███████╗██████╔╝███████╗███████╗███████╗███████╗██║░░██║");
        msg("╚═╝░░░░░╚═╝░░╚═╝╚═╝╚═╝░░░░░╚═╝╚══════╝╚═════╝░╚══════╝╚══════╝╚══════╝╚══════╝╚═╝░░╚═╝");
        msg("▀█░█▀ █▀▀ █▀▀█ █▀▀ ░▀░ █▀▀█ █▀▀▄ PrimeSeller-Fork v"+getPluginMeta().getVersion());
        msg("░█▄█░ █▀▀ █▄▄▀ ▀▀█ ▀█▀ █░░█ █░░█ | Maintainer: https://github.com/flyawaymaking");
        msg("░░▀░░ ▀▀▀ ▀░▀▀ ▀▀▀ ▀▀▀ ▀▀▀▀ ▀░░▀ | Server version: ("+ Bukkit.getServer().getVersion() + ")");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Expansions().register();
        }
    }

    @Override
    public void onDisable() {
        msg("██████╗░██████╗░██╗███╗░░░███╗███████╗░██████╗███████╗██╗░░░░░██╗░░░░░███████╗██████╗░");
        msg("██╔══██╗██╔══██╗██║████╗░████║██╔════╝██╔════╝██╔════╝██║░░░░░██║░░░░░██╔════╝██╔══██╗");
        msg("██████╔╝██████╔╝██║██╔████╔██║█████╗░░╚█████╗░█████╗░░██║░░░░░██║░░░░░█████╗░░██████╔╝");
        msg("██╔═══╝░██╔══██╗██║██║╚██╔╝██║██╔══╝░░░╚═══██╗██╔══╝░░██║░░░░░██║░░░░░██╔══╝░░██╔══██╗");
        msg("██║░░░░░██║░░██║██║██║░╚═╝░██║███████╗██████╔╝███████╗███████╗███████╗███████╗██║░░██║");
        msg("╚═╝░░░░░╚═╝░░╚═╝╚═╝╚═╝░░░░░╚═╝╚══════╝╚═════╝░╚══════╝╚══════╝╚══════╝╚══════╝╚═╝░░╚═╝");
        msg("▀█░█▀ █▀▀ █▀▀█ █▀▀ ░▀░ █▀▀█ █▀▀▄ PrimeSeller-Fork v"+getPluginMeta().getVersion());
        msg("░█▄█░ █▀▀ █▄▄▀ ▀▀█ ▀█▀ █░░█ █░░█ | Maintainer: https://github.com/flyawaymaking");
        msg("░░▀░░ ▀▀▀ ▀░▀▀ ▀▀▀ ▀▀▀ ▀▀▀▀ ▀░░▀ | Server version: ("+ Bukkit.getServer().getVersion() + ")");
        MapBase sql = new MapBase();
        sql.clear();
    }

    private void msg(String msg) {
        getLogger().info(msg);
    }

    private void loadManager(Manager manager, PrimeSeller plugin) {
        manager.init(plugin);
    }
}
