package com.github.doctrey.telegram.client.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class NameUtils {

    public static final String[] FIRST_NAMES = {"Ali", "Behnam", "Mohsen", "Azar", "Faranak"};
    public static final String[] LAST_NAMES = {"Alipour", "Abbasi", "Amiri", "Baani", "Barzi"};

    public static String randomFirstName() {
        int firstNameIndex = ThreadLocalRandom.current().nextInt(FIRST_NAMES.length);
        return FIRST_NAMES[firstNameIndex];
    }

    public static String randomLastName() {
        int lastNameIndex = ThreadLocalRandom.current().nextInt(LAST_NAMES.length);
        return LAST_NAMES[lastNameIndex];
    }
}
