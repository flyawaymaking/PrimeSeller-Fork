// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.menu.GuiMenu;
import me.byteswing.primeseller.util.Chat;

public class OpenCommand implements CommandExecutor {

    PrimeSeller main;

    public OpenCommand(PrimeSeller main) {
        main.getCommand("seller").setExecutor(this);
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("primeseller.seller")) {
                Chat.sendMessage(sender, Config.getMessage("commands.permission"));
                return true;
            }
            GuiMenu.open(player, main);
            player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 1, 1);
        }
        return true;
    }
}
