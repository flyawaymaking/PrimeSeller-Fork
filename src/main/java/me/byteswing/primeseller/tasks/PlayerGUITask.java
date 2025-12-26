/**
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

package me.byteswing.primeseller.tasks;

import me.byteswing.primeseller.menu.SellerMenu;
import me.byteswing.primeseller.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class PlayerGUITask extends BukkitRunnable {
    private final Player player;
    private final Inventory inv;

    public PlayerGUITask(@NotNull Inventory inv, @NotNull Player player) {
        this.player = player;
        this.inv = inv;
    }

    @Override
    public void run() {
        if (Util.update) {
            SellerMenu.updateSellMenu(inv, player);
            Util.update = false;
        }
    }
}
