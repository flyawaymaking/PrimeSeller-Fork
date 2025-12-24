// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.listeners.*;

public class ListenerManager implements Manager {

    @Override
    public void init(PrimeSeller plugin) {
        new SellerListener(plugin);
        new PlayerCloseListener(plugin);
        new AutoSellerListener(plugin);
    }
}
