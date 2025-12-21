// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.listeners;

import me.byteswing.primeseller.managers.AutoSellManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.util.Util;

public class PlayerJoinListener implements Listener {

    public PlayerJoinListener(PrimeSeller main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!Util.playerSellItems.containsKey(player.getUniqueId())) {
            Util.playerSellItems.put(player.getUniqueId(), 0);
        }
    }
}
