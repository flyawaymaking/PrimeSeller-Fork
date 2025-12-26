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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    public static boolean update = false;

    public static String limitedFormat = "Loading...";
    public static String unlimitedFormat = "Loading...";

    public static @NotNull String formattedTime(int time) {
        String defaultFormat = "yy-MM-dd HH:mm";
        String fmt = MainConfig.getConfig().getString("datetime-format", defaultFormat);
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat(fmt);
        } catch (IllegalArgumentException ex) {
            format = new SimpleDateFormat(defaultFormat);
        }
        long milliseconds = time * 1000L;

        String timeZone = MainConfig.getConfig().getString("time-zone");

        switch (timeZone) {
            case "GMT+2":
                timeZone = "Europe/Paris";
                break;
            case "GMT+1":
                timeZone = "Europe/London";
                break;
            case "GMT+0":
                timeZone = "UTC";
                break;
            case "GMT-1":
                timeZone = "Atlantic/Azores";
                break;
            case "GMT-2":
                timeZone = "America/Noronha";
                break;
            case "GMT-3":
                timeZone = "America/Argentina/Buenos_Aires";
                break;
            case "GMT-4":
                timeZone = "America/La_Paz";
                break;
            case "GMT-5":
                timeZone = "America/New_York";
                break;
            case "GMT-6":
                timeZone = "America/Chicago";
                break;
            case "GMT-7":
                timeZone = "America/Denver";
                break;
            case "GMT-8":
                timeZone = "America/Los_Angeles";
                break;
            case "GMT-9":
                timeZone = "America/Anchorage";
                break;
            case "GMT-10":
                timeZone = "Pacific/Honolulu";
                break;
            case "GMT-11":
                timeZone = "Pacific/Midway";
                break;
            case "GMT-12":
                timeZone = "Pacific/Kwajalein";
                break;
            case "GMT+4":
                timeZone = "Asia/Dubai";
                break;
            case "GMT+5":
                timeZone = "Asia/Karachi";
                break;
            case "GMT+6":
                timeZone = "Asia/Dhaka";
                break;
            case "GMT+7":
                timeZone = "Asia/Bangkok";
                break;
            case "GMT+8":
                timeZone = "Asia/Shanghai";
                break;
            case "GMT+9":
                timeZone = "Asia/Tokyo";
                break;
            case "GMT+10":
                timeZone = "Australia/Sydney";
                break;
            case "GMT+11":
                timeZone = "Pacific/Guadalcanal";
                break;
            case "GMT+12":
                timeZone = "Pacific/Fiji";
                break;
            case null:
                break;
            default:
                timeZone = "Europe/Moscow";
                break;
        }


        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        milliseconds += calendar.getTime().getTime() + calendar.getTimeZone().getRawOffset();
        return format.format(milliseconds);
    }

    public static int getMaterialAmount(@NotNull Player player, @NotNull Material material) {
        int count = 0;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            if (i == 36 || i == 37 || i == 38 || i == 39 || i == 40) {
                continue;
            }

            ItemStack stack = contents[i];
            if (stack != null && stack.getType() == material) {
                count += stack.getAmount();
            }
        }
        return count;
    }

    public static Map<Material, Integer> getMaterialsAmount(@NotNull Player player) {
        Map<Material, Integer> inventoryItems = new HashMap<>();

        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            if (i == 36 || i == 37 || i == 38 || i == 39 || i == 40) {
                continue;
            }
            ItemStack stack = contents[i];
            if (stack == null) continue;

            inventoryItems.merge(stack.getType(), stack.getAmount(), Integer::sum);
        }
        return inventoryItems;
    }
}
