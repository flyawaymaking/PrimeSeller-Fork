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

package me.byteswing.primeseller.commands.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import me.byteswing.primeseller.PrimeSeller;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PrimeSellerCompleter implements TabCompleter {

    public PrimeSellerCompleter(@NotNull PrimeSeller plugin) {
        plugin.getCommand("primeseller").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("update", "reload", "addlimited", "addunlimited");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("update")) {
            return Arrays.asList("limited", "unlimited");
        }
        boolean isAddUnlimited = args[0].equalsIgnoreCase("addlimited") || args[0].equalsIgnoreCase("addunlimited");
        if (args.length == 2 && isAddUnlimited) {
            return Arrays.asList("10", "50", "100");
        }
        if (args.length == 3 && isAddUnlimited) {
            return Arrays.asList("100", "500", "1000");
        }
        return null;
    }
}
