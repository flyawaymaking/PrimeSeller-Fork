// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.commands.OpenCommand;
import me.byteswing.primeseller.commands.PrimeSellerCommands;
import me.byteswing.primeseller.commands.tabcomplete.Completer;

public class CommandManager implements Manager {
    @Override
    public void init(PrimeSeller plugin) {
        new OpenCommand(plugin);
        new PrimeSellerCommands(plugin);
        new Completer(plugin);
    }
}
