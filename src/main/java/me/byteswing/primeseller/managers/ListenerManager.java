// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.listeners.PlayerCloseListener;
import me.byteswing.primeseller.listeners.PlayerJoinListener;
import me.byteswing.primeseller.listeners.SellerListener;

public class ListenerManager implements Manager {


    @Override
    public void init(PrimeSeller plugin) {
        new PlayerJoinListener(plugin);
        new SellerListener(plugin);
        new PlayerCloseListener(plugin);
    }
}
