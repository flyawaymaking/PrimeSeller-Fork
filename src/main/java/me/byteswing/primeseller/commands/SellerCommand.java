// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.commands;

import me.byteswing.primeseller.configurations.MessagesConfig;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.menu.SellerMenu;
import me.byteswing.primeseller.util.Chat;
import org.jetbrains.annotations.NotNull;

public class SellerCommand implements CommandExecutor {
    PrimeSeller plugin;

    public SellerCommand(@NotNull PrimeSeller plugin) {
        this.plugin = plugin;
        plugin.getCommand("seller").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.player-only"));
            return true;
        }

        if (!player.hasPermission("primeseller.seller")) {
            Chat.sendMessage(sender, MessagesConfig.getMessage("commands.no-permission"));
            return true;
        }

        SellerMenu.open(player, plugin);
        player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 1, 1);
        return true;
    }
}
