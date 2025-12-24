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

package me.byteswing.primeseller.managers;

import org.bukkit.plugin.Plugin;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.ItemsConfig;

public class ConfigManager {

    private static final ItemsConfig i = new ItemsConfig();
    private static final Config c = new Config();

    public static void loadConfigurations(Plugin plugin) {
        c.loadConfig(plugin);
        i.loadItemsYaml(plugin);
    }

    public static void reloadConfigurations() {
        Config.reloadConfig();
        ItemsConfig.reloadConfig();
    }
}
