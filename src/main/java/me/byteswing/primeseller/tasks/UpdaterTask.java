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

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.util.Updater;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class UpdaterTask extends BukkitRunnable {
    private final PrimeSeller plugin;
    private final boolean isLimited;

    public UpdaterTask(@NotNull PrimeSeller plugin, boolean isLimited) {
        this.plugin = plugin;
        this.isLimited = isLimited;
    }

    @Override
    public void run() {
        if (isLimited) {
            Updater.clearAndCreateLimited(plugin, false);
        } else {
            Updater.clearAndCreateUnLimited(plugin, false);
        }
    }
}
