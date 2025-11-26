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

import com.destroystokyo.paper.profile.ProfileProperty;
import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.configurations.Config;
import me.byteswing.primeseller.configurations.Items;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.configurations.database.SellItem;
import me.byteswing.primeseller.configurations.database.SkinData;
import com.destroystokyo.paper.profile.PlayerProfile;
import me.byteswing.primeseller.managers.AutoSellManager;
import net.kyori.adventure.text.Component;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    public static boolean update = false;

    private static final DecimalFormat format = new DecimalFormat("##.##");

    public static HashMap<UUID, Integer> playerSellItems = new HashMap<>();

    public static String limitedFormat = "Загрузка...";
    public static String unlimitedFormat = "Загрузка...";

    public static String formattedTime(int time) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
        long milliseconds = time * 1000L;

        String timeZone = Config.getConfig().getString("time-zone");

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

    public static void fillInventory(Inventory inv, Player player, PrimeSeller plugin) {
        MapBase sql = new MapBase();
        UUID playerId = player.getUniqueId();
        List<Component> unlim = new ArrayList<>();
        List<Component> lim = new ArrayList<>();
        List<Component> countdown = new ArrayList<>();
        for (Map.Entry<Integer, SellItem> entry : MapBase.database.entrySet()) {
            int next = entry.getKey();
            ItemStack item = entry.getValue().getItem().clone();
            if (sql.isLimited(next)) {
                double price = sql.getPrice(next);
                String price64 = format.format(price * 64).replace(",", ".");
                String priceall = format.format(Util.calc(player, item) * price).replace(",", ".");
                for (String s : Config.getMenuConfig().getStringList("lim-items.lore")) {
                    lim.add(Chat.toComponent(s
                            .replace("%price-x1%", format.format(price).replace(",", "."))
                            .replace("%price-x64%", price64)
                            .replace("%price-all%", priceall)
                            .replace("%sell%", String.valueOf(Util.playerSellItems.get(playerId)))
                            .replace("%max%", String.valueOf(Items.getConfig().getInt("limited.limit")))
                            .replace("%sell-items%", String.valueOf(sql.getSlot(next).getPlayerItemLimit(player)))
                            .replace("%max-items%", String.valueOf(Items.getConfig().getInt("limited.limit-per-items")
                            ))));
                }
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.lore(lim);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                }
                item.setItemMeta(meta);
                inv.setItem(next, item);
                lim.clear();
                continue;
            }
            double price = sql.getPrice(next);
            String price64 = format.format(price * 64).replace(",", ".");
            String priceall = format.format(Util.calc(player, item) * price).replace(",", ".");
            for (String s : Config.getMenuConfig().getStringList("unlim-items.lore")) {
                unlim.add(Chat.toComponent(s
                        .replace("%price-x1%", format.format(price).replace(",", "."))
                        .replace("%price-all%", priceall)
                        .replace("%price-x64%", price64)));
            }
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.lore(unlim);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
            ;
            item.setItemMeta(meta);
            inv.setItem(next, item);
            unlim.clear();
        }
        for (Integer i : Config.getMenuConfig().getIntegerList("divider.slots")) {
            String items = Config.getMenuConfig().getString("divider.material");
            List<Component> lore = Config.getMenuConfig().getStringList("divider.lore").stream()
                    .map(Chat::toComponent)
                    .toList();
            ItemStack item;
            try {
                item = new ItemStack(Material.valueOf(items));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Неизвестный предмет: " + items);
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.displayName(Chat.toComponent(Config.getMenuConfig().getString("divider.name")));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        for (Integer i : Config.getMenuConfig().getIntegerList("exit.slots")) {
            String items = Config.getMenuConfig().getString("exit.material");
            List<Component> lore = Config.getMenuConfig().getStringList("exit.lore").stream()
                    .map(Chat::toComponent)
                    .toList();
            ItemStack item;
            try {
                item = new ItemStack(Material.valueOf(items));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Неизвестный предмет: " + items);
                break;
            }
            ItemMeta meta = item.getItemMeta();
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.displayName(Chat.toComponent(Config.getMenuConfig().getString("exit.name")));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        for (Integer i : Config.getMenuConfig().getIntegerList("sell-inventory.slots")) {
            String items = Config.getMenuConfig().getString("sell-inventory.material");
            List<Component> lore = Config.getMenuConfig().getStringList("sell-inventory.lore").stream()
                    .map(Chat::toComponent)
                    .toList();
            ItemStack item;
            try {
                item = new ItemStack(Material.valueOf(items));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Неизвестный предмет: " + items);
                break;
            }
            ItemMeta meta = item.getItemMeta();
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.displayName(Chat.toComponent(Config.getMenuConfig().getString("sell-inventory.name")));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
        for (Integer i : Config.getMenuConfig().getIntegerList("countdown.slots")) {
            String material = Config.getMenuConfig().getString("countdown.material");
            ItemStack item = new ItemStack(Material.BARRIER);
            if (material != null) {
                if (material.startsWith("basehead-")) {
                    String url = material.replace("basehead-", "");
                    item = Util.getSkull(url);
                } else {
                    item = new ItemStack(Material.valueOf(material));
                }
            }
            ItemMeta meta = item.getItemMeta();
            for (String s : Config.getMenuConfig().getStringList("countdown.lore")) {
                countdown.add(Chat.toComponent(s
                        .replace("%lim-time%", Updater.getLimitedTime(2))
                        .replace("%unlim-time%", Updater.getUnLimitedTime(2))
                        .replace("%lim-time-format%", Util.limitedFormat)
                        .replace("%unlim-time-format%", Util.unlimitedFormat)));
            }
            meta.lore(countdown);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.displayName(Chat.toComponent(Config.getMenuConfig().getString("countdown.name")));
            item.setItemMeta(meta);
            inv.setItem(i, item);
            countdown.clear();
        }
        for (Integer i : Config.getMenuConfig().getIntegerList("autosell.slots")) {
            String items = Config.getMenuConfig().getString("autosell.material");
            List<Component> lore = Config.getMenuConfig().getStringList("autosell.lore").stream()
                    .map(line -> line
                            .replace("%autosell-slots%", String.valueOf(AutoSellManager.getAutoSellMaterials(player).size()))
                            .replace("%autosell-max-slots%", String.valueOf(AutoSellManager.getMaxAutoSellSlots(player))))
                    .map(Chat::toComponent)
                    .toList();
            ItemStack item;
            try {
                item = new ItemStack(Material.valueOf(items));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Неизвестный предмет: " + items);
                break;
            }
            ItemMeta meta = item.getItemMeta();
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.displayName(Chat.toComponent(Config.getMenuConfig().getString("autosell.name")));
            item.setItemMeta(meta);
            inv.setItem(i, item);
        }
    }

    public static int calc(Player p, ItemStack s) {
        int count = 0;
        for (int i = 0; i < p.getInventory().getSize(); ++i) {
            if (i != 40 && i != 38 && i != 37 && i != 36 && i != 39) {
                ItemStack stack = p.getInventory().getItem(i);
                if (stack != null && stack.isSimilar(s)) {
                    count += stack.getAmount();
                }
            }
        }
        return count;
    }

    private static SkinData decodeBase64(String url) {
        String json = new String(Base64.getDecoder().decode(url));

        Gson gson = new Gson();

        return gson.fromJson(json, SkinData.class);
    }

    public static ItemStack getSkull(String base64Texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (meta != null) {
            try {
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
                ProfileProperty property = new ProfileProperty("textures", base64Texture);
                profile.setProperty(property);

                meta.setPlayerProfile(profile);
            } catch (Exception ignored) {
                // Если не удалось установить текстуру, оставляем обычную голову
            }

            skull.setItemMeta(meta);
        }

        return skull;
    }
}
