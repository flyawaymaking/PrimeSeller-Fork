/**
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

package me.byteswing.primeseller.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class LanguageManager {

    private static final Gson gson = new GsonBuilder().create();
    private static final YamlConfiguration translations = new YamlConfiguration();

    public static void reload(JavaPlugin plugin, String lang) {
        lang = lang.toLowerCase();

        File file = new File(plugin.getDataFolder(), "translations/" + lang + ".yml");
        file.getParentFile().mkdirs();

        if (file.exists()) {
            try {
                translations.load(file);
                if (!translations.getKeys(false).isEmpty()) return;
            } catch (Exception ignored) {}
        }

        plugin.getLogger().info("Загрузка языка " + lang + "...");

        String version = plugin.getServer().getMinecraftVersion();
        String url = "https://api.github.com/repos/InventivetalentDev/minecraft-assets"
                + "/contents/assets/minecraft/lang/" + lang + ".json?ref=" + version;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = gson.fromJson(resp.body(), JsonObject.class);
            String base64Content = root.get("content").getAsString();

            JsonObject json = gson.fromJson(
                    new String(Base64Coder.decodeLines(base64Content)),
                    JsonObject.class
            );

            for (Map.Entry<String, JsonElement> e : json.entrySet()) {

                if (e.getKey().startsWith("item.minecraft.")) {
                    String name = e.getKey().replace("item.minecraft.", "");
                    if (name.contains(".")) continue;
                    translations.set("material." + name, e.getValue().getAsString());
                }

                if (e.getKey().startsWith("block.minecraft.")) {
                    String name = e.getKey().replace("block.minecraft.", "");
                    if (name.contains(".")) continue;
                    translations.set("material." + name, e.getValue().getAsString());
                }
            }

            translations.save(file);
            plugin.getLogger().info("Язык успешно загружен.");

        } catch (Exception ex) {
            plugin.getLogger().warning(ex.getMessage());
            plugin.getLogger().severe("Ошибка загрузки языка!");
        }
    }

    public static String translate(Material mat) {
        String key = mat.name().toLowerCase();
        String def = key.replace("_", " ");
        return translations.getString("material." + key, def);
    }
}
