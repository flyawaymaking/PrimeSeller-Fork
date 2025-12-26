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

package me.byteswing.primeseller.menu;

import me.byteswing.primeseller.PrimeSeller;
import me.byteswing.primeseller.managers.AutoSellerManager;
import me.byteswing.primeseller.managers.EconomyManager;
import me.byteswing.primeseller.util.MenuHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AutoSellerMenu {
    private static MenuHelper menuHelper;

    public static void init(@NotNull PrimeSeller plugin) {
        menuHelper = new MenuHelper(plugin, "autoseller-menu",
                "autosell-item", "divider", "toggle-button", "navigation");
    }

    public static @NotNull MenuHelper getMenuHelper() {
        return menuHelper;
    }

    public static int getCurrentPage(@NotNull Inventory inventory) {
        if (inventory.getHolder() instanceof AutoSellerInventoryHolder holder) {
            return holder.getCurrentPage();
        }
        return 0;
    }

    public static void openAutoSellMenu(@NotNull Player player) {
        openAutoSellMenu(player, 0);
    }

    public static void openAutoSellMenu(@NotNull Player player, int page) {
        AutoSellerInventoryHolder holder = new AutoSellerInventoryHolder();
        Inventory inv = Bukkit.createInventory(holder, menuHelper.getSize(), menuHelper.getTitle("%page%", page > 0 ? "[" + (page + 1) + "]" : ""));
        holder.setInventory(inv);
        holder.setCurrentPage(page);

        updateAutoSellMenu(player, inv, page);
        player.openInventory(inv);
    }

    public static void updateAutoSellMenu(@NotNull Player player, @NotNull Inventory inv, int page) {
        inv.clear();

        if (inv.getHolder() instanceof AutoSellerInventoryHolder holder) {
            holder.setCurrentPage(page);
        }

        Set<Material> allMaterials = AutoSellerManager.getAutoSellMaterials(player);
        List<Integer> itemSlots = menuHelper.getSlots("autosell-item");
        int totalPages = (int) Math.ceil((double) allMaterials.size() / itemSlots.size());
        String[] placeholders = {
                "%slots%", String.valueOf(allMaterials.size()),
                "%max-slots%", String.valueOf(AutoSellerManager.getMaxAutoSellSlots(player)),
                "%current-page%", String.valueOf(page + 1),
                "%total-pages%", String.valueOf(totalPages)
        };

        createAutoSellSlots(inv, player, page, allMaterials, itemSlots);
        createToggleButton(inv, player, placeholders);
        createNavigationButtons(inv, page, totalPages, placeholders);
        createCustomButtons(inv, placeholders);
        createDividers(inv, player, page, itemSlots);
    }

    private static void createAutoSellSlots(@NotNull Inventory inv, @NotNull Player player, int page,
                                            @NotNull Set<Material> allMaterials, @NotNull List<Integer> itemSlots) {
        List<Material> materialsList = new ArrayList<>(allMaterials);

        int startIndex = page * itemSlots.size();
        int endIndex = Math.min(startIndex + itemSlots.size(), materialsList.size());

        for (int i = startIndex; i < endIndex; i++) {
            Material material = materialsList.get(i);
            int slotIndex = i - startIndex;

            if (slotIndex >= itemSlots.size()) break;

            int itemsSold = AutoSellerManager.getItemsSoldForMaterial(player, material);
            double moneyEarned = AutoSellerManager.getMoneyEarnedForMaterial(player, material);

            menuHelper.addItemByMaterial(inv, "autosell-item", material, slotIndex,
                    "%items-sold%", String.valueOf(itemsSold),
                    "%money-earned%", EconomyManager.format(moneyEarned));

        }
    }

    private static void createToggleButton(@NotNull Inventory inv, @NotNull Player player, @NotNull String... placeholders) {
        boolean enabled = AutoSellerManager.isAutoSellEnabled(player);

        String path = enabled ? "toggle-button.enabled" : "toggle-button.disabled";
        ItemStack item = menuHelper.createCustomItem(path, placeholders);

        menuHelper.setItemToSlots(inv, "toggle-button", item);
    }

    private static void createNavigationButtons(@NotNull Inventory inv, int page, int totalPages, @NotNull String... placeholders) {
        if (page > 0) {
            String previousPagePath = "navigation.previous-page";
            ItemStack previousButton = menuHelper.createCustomItem(previousPagePath, placeholders);
            menuHelper.setItemToSlots(inv, previousPagePath, previousButton);
        }

        if (page < totalPages - 1) {
            String nextPagePath = "navigation.next-page";
            ItemStack previousButton = menuHelper.createCustomItem(nextPagePath, placeholders);
            menuHelper.setItemToSlots(inv, nextPagePath, previousButton);
        }
    }

    private static void createCustomButtons(@NotNull Inventory inv, @NotNull String... placeholders) {
        menuHelper.addCustomItems(inv, placeholders);
    }

    private static void createDividers(@NotNull Inventory inv, @NotNull Player player, int page, @NotNull List<Integer> itemSlots) {
        if (!menuHelper.isEnabled("divider")) return;
        ItemStack divider = menuHelper.createCustomItem("divider");

        int maxSlots = AutoSellerManager.getMaxAutoSellSlots(player);

        int slotsPerPage = itemSlots.size();
        int startGlobalSlot = page * slotsPerPage;

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) continue;

            if (itemSlots.contains(i)) {
                int slotIndexInPage = itemSlots.indexOf(i);
                int globalSlotIndex = startGlobalSlot + slotIndexInPage;
                if (globalSlotIndex >= maxSlots) {
                    inv.setItem(i, divider);
                }
            } else {
                inv.setItem(i, divider);
            }
        }
    }
}
