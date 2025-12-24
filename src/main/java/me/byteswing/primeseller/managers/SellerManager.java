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

package me.byteswing.primeseller.managers;

import me.byteswing.primeseller.configurations.MenuConfig;
import org.bukkit.Material;
import me.byteswing.primeseller.configurations.ItemsConfig;
import me.byteswing.primeseller.configurations.database.MapBase;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SellerManager {

    public static double generate(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 0.000000001);
    }

    public static void createUnLimItems() {
        List<String> unlimItems = ItemsConfig.getUnlimItems();
        List<Integer> unlimSlots = MenuConfig.getUnlimItemsSlots();

        for (Integer unlimSlot : unlimSlots) {
            if (unlimItems.isEmpty()) {
                break;
            }
            int random = (int) (Math.random() * unlimItems.size());
            String itemName = unlimItems.get(random);
            double min = ItemsConfig.getConfig().getDouble("unlimited.items." + itemName + ".min-price");
            double max = ItemsConfig.getConfig().getDouble("unlimited.items." + itemName + ".max-price");
            double price = generate(min, max);
            Material material = Material.getMaterial(itemName);
            if (material != null) {
                MapBase.saveMaterial(material, unlimSlot, price, true);
            }
            unlimItems.remove(random);
        }
    }

    public static void createLimItems() {
        List<String> limItems = ItemsConfig.getLimItems();
        List<Integer> limSlots = MenuConfig.getLimItemsSlots();

        for (Integer limSlot : limSlots) {
            if (limItems.isEmpty()) {
                break;
            }
            int random = (int) (Math.random() * limItems.size());
            String itemName = limItems.get(random);
            double min = ItemsConfig.getConfig().getDouble("limited.items." + itemName + ".min-price");
            double max = ItemsConfig.getConfig().getDouble("limited.items." + itemName + ".max-price");
            double price = generate(min, max);
            Material material = Material.getMaterial(itemName);
            if (material != null) {
                MapBase.saveMaterial(material, limSlot, price, true);
            }
            limItems.remove(random);
        }
    }
}
