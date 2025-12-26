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

package me.byteswing.primeseller.commands;

import me.byteswing.primeseller.configurations.MessagesConfig;
import me.byteswing.primeseller.managers.EconomyManager;
import me.byteswing.primeseller.managers.LanguageManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.ItemsConfig;
import me.byteswing.primeseller.managers.ConfigManager;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Updater;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrimeSellerCommands implements CommandExecutor {
    private final PrimeSeller plugin;

    public PrimeSellerCommands(@NotNull PrimeSeller plugin) {
        this.plugin = plugin;
        plugin.getCommand("primeseller").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("primeseller.admin")) {
            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.no-permission"));
            return true;
        }

        if (args.length == 0) {
            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.update-use"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "update":
                if (args.length > 1) {
                    switch (args[1].toLowerCase()) {
                        case "limited":
                            Updater.clearAndCreateLimited(plugin, true);
                            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.update-lim"));
                            return true;
                        case "unlimited":
                            Updater.clearAndCreateUnLimited(plugin, true);
                            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.update-unlim"));
                            return true;
                        default:
                            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.update-usage"));
                            return true;
                    }
                } else {
                    Updater.update(plugin);
                    Chat.sendMessage(sender, MessagesConfig.getMessage("commands.update"));
                    return true;
                }
            case "reload":
                reloadConfig();
                Chat.sendMessage(sender, MessagesConfig.getMessage("commands.reload"));
                return true;
        }

        if (!(sender instanceof Player player)) {
            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.player-only"));
            return true;
        }

        return handleItemCommands(player, subCommand, args);
    }

    private boolean handleItemCommands(@NotNull Player player, @NotNull String subCommand, @NotNull String[] args) {
        if (args.length < 3) {
            String messagePath = subCommand.equals("addlimited")
                    ? "commands.addlimited-use"
                    : "commands.addunlimited-use";
            Chat.sendMessage(player, MessagesConfig.getMessage(messagePath));
            return true;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType().isAir()) {
            Chat.sendMessage(player, MessagesConfig.getMessage("commands.additem-error")
                    .replace("%material%", LanguageManager.translate(handItem.getType())));
            return true;
        }

        Double minPrice = parsePrice(args[1]);
        Double maxPrice = parsePrice(args[2]);
        if (minPrice == null || maxPrice == null) {
            Chat.sendMessage(player, MessagesConfig.getMessage("commands.not-number"));
            return true;
        }

        boolean isLimited = subCommand.equals("addlimited");
        ItemsConfig.addItem(handItem, minPrice, maxPrice, isLimited);

        sendAddedMessage(player, LanguageManager.translate(handItem.getType()), minPrice, maxPrice);
        return true;
    }

    private void reloadConfig() {
        ConfigManager.reloadConfigurations();
        LanguageManager.reload(plugin);
        EconomyManager.reload();
        Chat.init(plugin);
    }

    private @Nullable Double parsePrice(@NotNull String priceStr) {
        try {
            return Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void sendAddedMessage(@NotNull Player player, @NotNull String itemName, double minPrice, double maxPrice) {
        String message = MessagesConfig.getMessage("commands.added")
                .replace("%item%", itemName)
                .replace("%min-price%", EconomyManager.format(minPrice))
                .replace("%max-price%", EconomyManager.format(maxPrice));
        Chat.sendMessage(player, message);
    }
}
