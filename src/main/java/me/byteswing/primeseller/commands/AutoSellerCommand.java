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

package me.byteswing.primeseller.commands;

import me.byteswing.primeseller.configurations.MessagesConfig;
import me.byteswing.primeseller.managers.AutoSellerManager;
import me.byteswing.primeseller.menu.AutoSellerMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.util.Chat;
import org.jetbrains.annotations.NotNull;

public class AutoSellerCommand implements CommandExecutor {

    public AutoSellerCommand(@NotNull PrimeSeller plugin) {
        plugin.getCommand("autoseller").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.player-only"));
            return true;
        }

        if (!player.hasPermission("primeseller.autoseller")) {
            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.no-permission"));
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                AutoSellerManager.setAutoSellEnabled(player, true);
                Chat.sendMessage(player, MessagesConfig.getMessage("autosell.enabled"));
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                AutoSellerManager.setAutoSellEnabled(player, false);
                Chat.sendMessage(player, MessagesConfig.getMessage("autosell.disabled"));
                return true;
            } else if (args[0].equalsIgnoreCase("toggle")) {
                AutoSellerManager.toggleAutoSell(player);
                boolean enabled = AutoSellerManager.isAutoSellEnabled(player);
                Chat.sendMessage(player, enabled ?
                        MessagesConfig.getMessage("autosell.enabled") :
                        MessagesConfig.getMessage("autosell.disabled"));
                return true;
            }
        }
        AutoSellerMenu.openAutoSellMenu(player);
        return true;
    }
}
