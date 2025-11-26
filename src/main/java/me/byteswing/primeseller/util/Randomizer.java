// Modified by flyawaymaking (https://github.com/flyawaymaking)
// Original copyright: 2025 destroydevs (https://github.com/destroydevs/primeseller)
// Licensed under Apache License 2.0

package me.byteswing.primeseller.util;

public class Randomizer {

    public static String randomString(int length) {

        String regex = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";

        char[] chars = regex.toCharArray();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int c = (int) (Math.random() * chars.length);
            builder.append(chars[c]);
        }
        return builder.toString();
    }

}
