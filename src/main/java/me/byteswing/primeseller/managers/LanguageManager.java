package me.byteswing.primeseller.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class LanguageManager {
    private static JavaPlugin plugin;
    private static ResourceBundle bundle;

    public static void init(JavaPlugin plugin) {
        LanguageManager.plugin = plugin;
        try {
            bundle = ResourceBundle.getBundle("lang.ru_ru");

            plugin.getLogger().info("Русский перевод загружен");

        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка загрузки перевода: " + e.getMessage());
        }
    }

    public static String translate(@NotNull String key, @NotNull Locale locale) {
        if (locale.toString().toLowerCase().contains("ru") && bundle != null) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException e) {
                plugin.getLogger().warning("Не найден перевод для ключа: " + key);
            }
        }
        Component translated = GlobalTranslator.render(Component.translatable(key), locale);
        return PlainTextComponentSerializer.plainText().serialize(translated);
    }
}
