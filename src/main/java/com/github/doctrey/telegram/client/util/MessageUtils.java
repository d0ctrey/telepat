package com.github.doctrey.telegram.client.util;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Soheil on 12/28/17.
 */
public class MessageUtils {

    public static int generateRandomId() {
        return ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String generateRandomAlphaNumeric(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}
