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

package me.byteswing.primeseller.menu;

import me.byteswing.primeseller.configurations.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import me.byteswing.primeseller.configurations.Items;
import me.byteswing.primeseller.configurations.database.MapBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class SellerMenu {

    public static double generate(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max + 0.000000001);
    }

    public static void createUnLimItems() {
        if (!Items.getConfig().contains("unlimited.items")) return;
        List<String> randomItems = new ArrayList<>(Items.getConfig().getConfigurationSection("unlimited.items").getKeys(false));
        List<Integer> unlimSlots = new ArrayList<>(Config.getMenuConfig().getIntegerList("unlim-items.slots"));

        for (Integer unlimSlot : unlimSlots) {
            if (randomItems.isEmpty()) {
                break;
            }
            int random = (int) (Math.random() * randomItems.size());
            String item = randomItems.get(random);
            double min = Double.parseDouble(Items.getConfig().getString("unlimited.items." + item + ".min-price").replace(",", "."));
            double max = Double.parseDouble(Items.getConfig().getString("unlimited.items." + item + ".max-price").replace(",", "."));
            int lim = Items.getConfig().getInt("limited.limit-per-items");
            double price = generate(min, max);
            try {
                MapBase.saveMaterial(new ItemStack(Material.valueOf(item.toUpperCase(Locale.ENGLISH))), unlimSlot, price, lim, false);
            } catch (IllegalArgumentException e) {
                ItemStack itemStack = Items.getConfig().getItemStack("unlimited.items." + item + ".item");
                MapBase.saveMaterial(itemStack, unlimSlot, price, lim, false);
            }
            randomItems.remove(random);
        }
    }

    public static void createLimItems() {
        if (!Items.getConfig().contains("limited.items")) return;
        List<String> randomItems = new ArrayList<>(Items.getConfig().getConfigurationSection("limited.items").getKeys(false));
        List<Integer> limSlots = new ArrayList<>(Config.getMenuConfig().getIntegerList("lim-items.slots"));

        for (int i = 0; i < limSlots.size(); i++) {
            if (randomItems.isEmpty()) {
                break;
            }
            int random = (int) (Math.random() * randomItems.size());
            String item = randomItems.get(random);
            double min = Double.parseDouble(Items.getConfig().getString("limited.items." + item + ".min-price").replace(",", "."));
            double max = Double.parseDouble(Items.getConfig().getString("limited.items." + item + ".max-price").replace(",", "."));
            int lim = Items.getConfig().getInt("limited.limit-per-items");
            double price = generate(min, max);
            try {
                MapBase.saveMaterial(new ItemStack(Material.valueOf(item)), limSlots.get(i), price, lim, true);
            } catch (IllegalArgumentException e) {
                ItemStack itemStack = Items.getConfig().getItemStack("limited.items." + item + ".item");
                MapBase.saveMaterial(itemStack, limSlots.get(i), price, lim, true);
            }
            randomItems.remove(random);
            if (i == limSlots.size()) {
                limSlots.clear();
                randomItems.clear();
            }
        }
    }
}
