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

    public static double calculateSellPrice(int itemSlot, int amount) {
        return simulate(itemSlot, amount, false);
    }

    public static void takePrice(int itemSlot, int count) {
        simulate(itemSlot, count, true);
    }

    private static double simulate(int itemSlot, int amount, boolean apply) {
        SellItem sellItem = MapBase.get(itemSlot);

        if (sellItem == null || amount <= 0) {
            return 0;
        }

        double currentPrice = sellItem.getPrice();

        if (!MainConfig.isUnderstandingEnabled()) {
            return currentPrice * amount;
        }

        int threshold = MainConfig.getUnderstandingPriceItems();

        if (threshold <= 0) {
            return currentPrice * amount;
        }

        standardPrice.putIfAbsent(itemSlot, currentPrice);

        double originalPrice = standardPrice.get(itemSlot);
        double minPrice = originalPrice * MainConfig.getUnderstandingPriceMinPercent() / 100.0;
        double percent = MainConfig.getUnderstandingPricePercent();

        int counter = soldItemsCount.getOrDefault(itemSlot, 0);

        if (apply) {
            counter += amount;

            int batches = counter / threshold;
            counter %= threshold;

            double newPrice = currentPrice;

            for (int i = 0; i < batches && newPrice > minPrice; i++) {
                newPrice = Math.max(newPrice - newPrice * percent / 100.0, minPrice);
            }

            sellItem.setPrice(newPrice);
            soldItemsCount.put(itemSlot, counter);

            return currentPrice * amount;
        }

        double simulatedPrice = currentPrice;
        double total = 0;

        for (int i = 0; i < amount; i++) {
            total += simulatedPrice;

            if (++counter >= threshold) {
                counter = 0;

                if (simulatedPrice > minPrice) {
                    simulatedPrice = Math.max(simulatedPrice - simulatedPrice * percent / 100.0, minPrice);
                }
            }
        }

        return total;
    }

    public static void resetCounters() {
        soldItemsCount.clear();
    }
}
