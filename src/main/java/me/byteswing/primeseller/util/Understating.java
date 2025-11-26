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

import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.database.MapBase;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Understating {

    public static final HashMap<Integer, Double> standardPrice = new HashMap<>();
    private static final Map<Integer, Integer> soldItemsCount = new HashMap<>();

    private static final DecimalFormat format = new DecimalFormat("#.##");

    public static void takePrice(int item, int count) {
        if (!Config.getConfig().getBoolean("understating-price.enable")) {
            return;
        }

        int itemsThreshold = Config.getConfig().getInt("understating-price.items", 1);

        // Увеличиваем счетчик проданных предметов
        soldItemsCount.put(item, soldItemsCount.getOrDefault(item, 0) + count);

        // Проверяем, достигли ли порога для снижения цены
        if (soldItemsCount.get(item) < itemsThreshold) {
            return;
        }

        // Сбрасываем счетчик и снижаем цену
        int batches = soldItemsCount.get(item) / itemsThreshold;
        soldItemsCount.put(item, soldItemsCount.get(item) % itemsThreshold);

        MapBase h2 = new MapBase();
        if (!standardPrice.containsKey(item)) {
            standardPrice.put(item, h2.getPrice(item));
        }

        double currentPrice = h2.getPrice(item);
        double originalPrice = standardPrice.get(item);

        // Рассчитываем процент снижения
        double percent = Double.parseDouble(Config.getConfig().getString("understating-price.percent", "0.01").replace(",", "."));
        int minPercent = Config.getConfig().getInt("understating-price.min-percent", 10);

        // Рассчитываем минимальную допустимую цену
        double minPrice = originalPrice * minPercent / 100.0;

        // Проверяем, не достигли ли мы уже минимальной цены
        if (currentPrice <= minPrice) {
            return;
        }

        // Рассчитываем общее снижение с учетом минимальной цены
        double totalReduction = 0;
        double tempPrice = currentPrice; // Временная переменная для расчетов

        for (int i = 0; i < batches; i++) {
            double reduction = tempPrice * percent / 100.0;

            // Гарантируем минимальное снижение хотя бы на 0.01
            if (reduction < 0.01) {
                reduction = 0.01;
            }

            // Проверяем, не упадет ли цена ниже минимума
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
            h2.setPrice(item, Double.parseDouble(format.format(newPrice).replace(",", ".")));
        }
    }

    public static void resetCounters() {
        soldItemsCount.clear();
    }
}
