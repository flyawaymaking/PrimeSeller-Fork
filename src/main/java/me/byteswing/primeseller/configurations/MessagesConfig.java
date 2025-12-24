package me.byteswing.primeseller.configurations;

import java.util.List;

public class MessagesConfig {
    public static String getMessage(String key) {
        // todo
        return config.getString("messages." + key, "<red>message-" + key + ": not found");
    }

    public static List<String> getMessageList(String key) {
        // todo
        return config.getStringList("messages." + key);
    }
}
