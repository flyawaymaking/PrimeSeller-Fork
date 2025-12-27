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

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.database.SellItem;
import me.byteswing.primeseller.configurations.database.UnlimSoldItems;
import me.byteswing.primeseller.util.Understating;
import org.bukkit.Material;
import me.byteswing.primeseller.configurations.ItemsConfig;
import me.byteswing.primeseller.configurations.database.MapBase;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class SellerManager {

    public static double generate(double min, double max) {
        if (min > max) {
            return min;
        }
        return ThreadLocalRandom.current().nextDouble(min, max + 0.000000001);
    }

    public static void createUnLimItems(@NotNull PrimeSeller plugin) {
        List<String> unlimItems = ItemsConfig.getUnlimItems();
        List<Integer> unlimSlots = plugin.getSellerMenuHelper().getSlots("unlim-item");

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
            if (material != null && !material.isAir() && material.isItem()) {
                MapBase.saveMaterial(material, unlimSlot, price, false);
            } else {
                plugin.getLogger().info("Material " + itemName + " wasn't added to unlimited items!");
            }
            unlimItems.remove(random);
        }
    }

    public static void createLimItems(@NotNull PrimeSeller plugin) {
        List<String> limItems = ItemsConfig.getLimItems();
        List<Integer> limSlots = plugin.getSellerMenuHelper().getSlots("lim-item");

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
            if (material != null && !material.isAir() && material.isItem()) {
                MapBase.saveMaterial(material, limSlot, price, true);
            } else {
                plugin.getLogger().info("Material " + itemName + " wasn't added to limited items!");
            }
            limItems.remove(random);
        }
    }

    public static @NotNull SoldData sellLimItem(@NotNull Player player, @NotNull SellItem sellItem, int count) {
        UUID playerId = player.getUniqueId();
        int selledItems = UnlimSoldItems.get(playerId);
        int itemLimit = sellItem.getPlayerItemLimit(playerId);
        int totalLimit = ItemsConfig.getConfig().getInt("limited.limit");
        int itemLimitPerItems = ItemsConfig.getConfig().getInt("limited.limit-per-items");

        int availableToSell = Math.min(totalLimit - selledItems, itemLimitPerItems - itemLimit);

        if (count > availableToSell) {
            count = availableToSell;
        }

        if (count <= 0) {
            return new SoldData(0, 0);
        }

        UnlimSoldItems.put(playerId, selledItems + count);
        sellItem.addItemLimit(playerId, count);

        player.getInventory().removeItem(ItemStack.of(sellItem.getMaterial(), count));
        Understating.takePrice(sellItem.getSlot(), count);
        return new SoldData(sellItem.getPrice() * count, count);
    }

    public static @NotNull SoldData sellUnlimItem(@NotNull Player player, @NotNull SellItem sellItem, int count) {
        player.getInventory().removeItem(ItemStack.of(sellItem.getMaterial(), count));
        Understating.takePrice(sellItem.getSlot(), count);
        return new SoldData(sellItem.getPrice() * count, count);
    }

    public static class SoldData {
        public double price;
        public int amount;

        public SoldData(double price, int amount) {
            this.price = price;
            this.amount = amount;
        }
    }
}
