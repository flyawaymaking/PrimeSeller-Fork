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

package me.byteswing.primeseller.util;

import me.byteswing.primeseller.configurations.MainConfig;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.configurations.database.SellItem;

import java.util.HashMap;
import java.util.Map;

public class Understating {

    public static final HashMap<Integer, Double> standardPrice = new HashMap<>();
    private static final Map<Integer, Integer> soldItemsCount = new HashMap<>();

    public static void takePrice(int itemSlot, int count) {
        if (!MainConfig.isUnderstandingEnabled()) {
            return;
        }

        int itemsThreshold = MainConfig.getUnderstandingPriceItems();

        soldItemsCount.put(itemSlot, soldItemsCount.getOrDefault(itemSlot, 0) + count);

        if (soldItemsCount.get(itemSlot) < itemsThreshold) {
            return;
        }

        int batches = soldItemsCount.get(itemSlot) / itemsThreshold;
        soldItemsCount.put(itemSlot, soldItemsCount.get(itemSlot) % itemsThreshold);

        SellItem sellItem = MapBase.get(itemSlot);
        if (!standardPrice.containsKey(itemSlot)) {
            standardPrice.put(itemSlot, sellItem.getPrice());
        }

        double currentPrice = sellItem.getPrice();
        double originalPrice = standardPrice.get(itemSlot);

        double percent = MainConfig.getUnderstandingPricePercent();
        int minPercent = MainConfig.getUnderstandingPriceMinPercent();

        double minPrice = originalPrice * minPercent / 100.0;

        if (currentPrice <= minPrice) {
            return;
        }

        double totalReduction = 0;
        double tempPrice = currentPrice;

        for (int i = 0; i < batches; i++) {
            double reduction = tempPrice * percent / 100.0;

            if (tempPrice - reduction < minPrice) {
                totalReduction += (tempPrice - minPrice);
                break;
            } else {
                totalReduction += reduction;
                tempPrice -= reduction;
            }
        }

        if (totalReduction > 0) {
            double newPrice = currentPrice - totalReduction;
            newPrice = Math.max(minPrice, newPrice);
            sellItem.setPrice(newPrice);
        }
    }

    public static void resetCounters() {
        soldItemsCount.clear();
    }
}
