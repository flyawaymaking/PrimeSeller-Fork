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

package me.byteswing.primeseller.economy;

import me.byteswing.primeseller.PrimeSeller;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.DecimalFormat;

public class VaultEconomy implements EconomyProvider {
    private final PrimeSeller plugin;
    private final Economy economy;
    private final DecimalFormat format = new DecimalFormat("##.##");

    public VaultEconomy(PrimeSeller plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault plugin not found!");
            economy = null;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("No economy provider found for Vault!");
            economy = null;
            return;
        }

        economy = rsp.getProvider();
        plugin.getLogger().info("Vault economy provider: " + economy.getName());
    }

    public void addBalance(Player player, double amount) {
        if (economy == null) {
            return;
        }

        try {
            economy.depositPlayer(player, amount);
        } catch (Exception e) {
            plugin.getLogger().warning("Vault addBalance error: " + e.getMessage());
        }
    }

    public String format(double amount) {
        if (economy == null) {
            return format.format(amount);
        }
        return economy.format(Double.parseDouble(format.format(amount).replace(",", ".")));
    }

    public boolean isAvailable() {
        return economy != null;
    }
}
