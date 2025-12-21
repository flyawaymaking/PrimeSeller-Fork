package me.byteswing.primeseller;

import me.byteswing.primeseller.managers.*;
import me.byteswing.primeseller.configurations.database.MapBase;
import me.byteswing.primeseller.menu.AutoSellMenu;
import me.byteswing.primeseller.menu.GuiMenu;
import me.byteswing.primeseller.util.Chat;
import me.byteswing.primeseller.util.Updater;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PrimeSeller extends JavaPlugin {

    @Override
    public void onEnable() {
        msg("██████╗░██████╗░██╗███╗░░░███╗███████╗░██████╗███████╗██╗░░░░░██╗░░░░░███████╗██████╗░");
        msg("██╔══██╗██╔══██╗██║████╗░████║██╔════╝██╔════╝██╔════╝██║░░░░░██║░░░░░██╔════╝██╔══██╗");
        msg("██████╔╝██████╔╝██║██╔████╔██║█████╗░░╚█████╗░█████╗░░██║░░░░░██║░░░░░█████╗░░██████╔╝");
        msg("██╔═══╝░██╔══██╗██║██║╚██╔╝██║██╔══╝░░░╚═══██╗██╔══╝░░██║░░░░░██║░░░░░██╔══╝░░██╔══██╗");
        msg("██║░░░░░██║░░██║██║██║░╚═╝░██║███████╗██████╔╝███████╗███████╗███████╗███████╗██║░░██║");
        msg("╚═╝░░░░░╚═╝░░╚═╝╚═╝╚═╝░░░░░╚═╝╚══════╝╚═════╝░╚══════╝╚══════╝╚══════╝╚══════╝╚═╝░░╚═╝");
        msg("▀█░█▀ █▀▀ █▀▀█ █▀▀ ░▀░ █▀▀█ █▀▀▄ PrimeSeller v" + getPluginMeta().getVersion());
        msg("░█▄█░ █▀▀ █▄▄▀ ▀▀█ ▀█▀ █░░█ █░░█ | Developer: Telegram: @byteswing");
        msg("░░▀░░ ▀▀▀ ▀░▀▀ ▀▀▀ ▀▀▀ ▀▀▀▀ ▀░░▀ | Server version: (" + Bukkit.getServer().getVersion() + ")");
        msg("Contributors: golovin12");

        ConfigManager.loadConfigurations(this);
        saveDefaultConfig();
        EconomyManager.init(this);
        if (!EconomyManager.isEconomyAvailable()) {
            getLogger().severe("Plugin disabled - economy system not available");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Chat.init(this);
        Updater.start(this);
        LanguageManager.reload(this);
        AutoSellManager.init(this);
        loadManager(new ListenerManager(), this);
        loadManager(new CommandManager(), this);
        AutoSellMenu.init(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Expansions().register();
        }
    }

    @Override
    public void onDisable() {
        AutoSellManager.disable();
        Updater.stop();
        GuiMenu.disable();
        MapBase sql = new MapBase();
        sql.clear();
        msg("██████╗░██████╗░██╗███╗░░░███╗███████╗░██████╗███████╗██╗░░░░░██╗░░░░░███████╗██████╗░");
        msg("██╔══██╗██╔══██╗██║████╗░████║██╔════╝██╔════╝██╔════╝██║░░░░░██║░░░░░██╔════╝██╔══██╗");
        msg("██████╔╝██████╔╝██║██╔████╔██║█████╗░░╚█████╗░█████╗░░██║░░░░░██║░░░░░█████╗░░██████╔╝");
        msg("██╔═══╝░██╔══██╗██║██║╚██╔╝██║██╔══╝░░░╚═══██╗██╔══╝░░██║░░░░░██║░░░░░██╔══╝░░██╔══██╗");
        msg("██║░░░░░██║░░██║██║██║░╚═╝░██║███████╗██████╔╝███████╗███████╗███████╗███████╗██║░░██║");
        msg("╚═╝░░░░░╚═╝░░╚═╝╚═╝╚═╝░░░░░╚═╝╚══════╝╚═════╝░╚══════╝╚══════╝╚══════╝╚══════╝╚═╝░░╚═╝");
        msg("▀█░█▀ █▀▀ █▀▀█ █▀▀ ░▀░ █▀▀█ █▀▀▄ PrimeSeller v" + getPluginMeta().getVersion());
        msg("░█▄█░ █▀▀ █▄▄▀ ▀▀█ ▀█▀ █░░█ █░░█ | Developer: Telegram: @byteswing");
        msg("░░▀░░ ▀▀▀ ▀░▀▀ ▀▀▀ ▀▀▀ ▀▀▀▀ ▀░░▀ | Server version: (" + Bukkit.getServer().getVersion() + ")");
        msg("Contributors: golovin12");
    }

    private void msg(String msg) {
        getLogger().info(msg);
    }

    private void loadManager(Manager manager, PrimeSeller plugin) {
        manager.init(plugin);
    }
}
