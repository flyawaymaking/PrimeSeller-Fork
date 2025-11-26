/**
 * Copyright 2025 destroydevs (https://github.com/destroydevs/primeseller)
 * Copyright 2025 flyawaymaking (https://github.com/flyawaymaking)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *     http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// This file was modified by flyawaymaking (https://github.com/flyawaymaking) from the original version.

package me.byteswing.primeseller.util;

import me.byteswing.primeseller.PrimeSeller;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

public class Chat {
    private static String prefix = "";
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static void init(PrimeSeller plugin) {
        prefix = plugin.getConfig().getString("prefix", "<gradient:#5637bc:#9258ff>SELLER</gradient> <#b9b9b9>|");
    }

    public static Component toComponent(String message) {
        return miniMessage.deserialize(message);
    }

    public static void sendMessage(CommandSender sender, String msg) {
        if (msg == null || msg.isEmpty()) return;
        sender.sendMessage(toComponent(prefix + " " + msg));
    }

    public static void broadcast(List<String> messages) {
        if (messages.isEmpty()) return;
        String combinedMessage = String.join("\n", messages);
        Bukkit.broadcast(toComponent(combinedMessage));
    }
}
