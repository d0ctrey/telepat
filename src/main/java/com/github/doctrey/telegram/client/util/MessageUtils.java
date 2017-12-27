package com.github.doctrey.telegram.client.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Soheil on 12/28/17.
 */
public class MessageUtils {

    public static int generateRandomId() {
        return ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

}
