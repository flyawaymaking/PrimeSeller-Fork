// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.commands.AutoSellerCommand;
import me.byteswing.primeseller.commands.SellerCommand;
import me.byteswing.primeseller.commands.PrimeSellerCommands;
import me.byteswing.primeseller.commands.tabcomplete.AutoSellCompleter;
import me.byteswing.primeseller.commands.tabcomplete.PrimeSellerCompleter;
import org.jetbrains.annotations.NotNull;

public class CommandManager implements Manager {
    @Override
    public void init(@NotNull PrimeSeller plugin) {
        new SellerCommand(plugin);
        new PrimeSellerCommands(plugin);
        new PrimeSellerCompleter(plugin);
        new AutoSellerCommand(plugin);
        new AutoSellCompleter(plugin);
    }
}
