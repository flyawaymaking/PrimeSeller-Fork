// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.menu.GuiMenu;

import java.util.UUID;

public class PlayerCloseListener implements Listener {

    public PlayerCloseListener(PrimeSeller main) {
        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if (GuiMenu.tasks.containsKey(id)) {
            GuiMenu.tasks.get(id).cancel();
            GuiMenu.tasks.remove(id);
        }
    }
}
