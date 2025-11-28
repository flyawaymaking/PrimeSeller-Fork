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

import me.byteswing.primeseller.managers.LanguageManager;
import me.byteswing.primeseller.util.Eco;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.Items;
import me.byteswing.primeseller.managers.ConfigManager;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Updater;

public class PrimeSellerCommands implements CommandExecutor {
    private final PrimeSeller plugin;

    public PrimeSellerCommands(PrimeSeller main) {
        this.plugin = main;
        main.getCommand("primeseller").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("primeseller.admin")) {
            Chat.sendMessage(sender, Config.getMessage("commands.permission"));
            return true;
        }

        if (args.length == 0) {
            Chat.sendMessage(sender, Config.getMessage("commands.update-use"));
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "update":
                if (args.length > 1) {
                    switch (args[1].toLowerCase()) {
                        case "limited":
                            Updater.clearAndCreateLimited(plugin, true);
                            Chat.sendMessage(sender, Config.getMessage("commands.update-lim"));
                            return true;
                        case "unlimited":
                            Updater.clearAndCreateUnLimited(plugin, true);
                            Chat.sendMessage(sender, Config.getMessage("commands.update-unlim"));
                            return true;
                        default:
                            Chat.sendMessage(sender, Config.getMessage("commands.update-usage"));
                            return true;
                    }
                } else {
                    Updater.update(plugin);
                    Chat.sendMessage(sender, Config.getMessage("commands.update"));
                    return true;
                }
            case "reload":
                reloadConfig();
                Chat.sendMessage(sender, Config.getMessage("commands.reload"));
                return true;
        }

        if (!(sender instanceof Player player)) {
            Chat.sendMessage(sender, Config.getMessage("commands.player-only"));
            return true;
        }

        return handleItemCommands(player, subCommand, args);
    }

    private boolean handleItemCommands(Player player, String subCommand, String[] args) {
        if (args.length < 3) {
            sendUsageMessages(player);
            return true;
        }

        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() == Material.AIR) {
            String errorPath = subCommand.equals("addlimited")
                    ? "commands.addlimited-error"
                    : "commands.addunlimited-error";
            Chat.sendMessage(player, Config.getMessage(errorPath));
            return true;
        }

        Double minPrice = parsePrice(args[1]);
        Double maxPrice = parsePrice(args[2]);
        if (minPrice == null || maxPrice == null) {
            Chat.sendMessage(player, Config.getMessage("commands.not-number"));
            return true;
        }

        boolean isLimited = subCommand.equals("addlimited");
        Items.addItem(handItem, minPrice, maxPrice, isLimited);

        sendAddedMessage(player, LanguageManager.translate(handItem.getType()), minPrice, maxPrice);
        return true;
    }

    private void reloadConfig() {
        ConfigManager.reloadConfigurations();
        LanguageManager.reload(plugin);
        Eco.init(plugin);
        Chat.init(plugin);
    }

    private Double parsePrice(String priceStr) {
        try {
            return Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void sendUsageMessages(Player player) {
        Chat.sendMessage(player, Config.getMessage("commands.addlimited-use"));
        Chat.sendMessage(player, Config.getMessage("commands.addunlimited-use"));
    }

    private void sendAddedMessage(Player player, String itemName, double minPrice, double maxPrice) {
        String message = Config.getMessage("commands.added")
                .replace("%item%", itemName)
                .replace("%min-price%", Eco.format(minPrice))
                .replace("%max-price%", Eco.format(maxPrice));
        Chat.sendMessage(player, message);
    }
}
