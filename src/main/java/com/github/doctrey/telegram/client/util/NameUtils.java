package com.github.doctrey.telegram.client.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class NameUtils {

    public static final String[] FIRST_NAMES = {"Ali", "Behnam", "Mohsen", "Azar", "Faranak", "Saman", "Ebrahim", "Vahid", "Rojin", "Rozita", "Maryam", "Neda", "Sahar", "Sara", "Nasim", "Nima", "Hamid", "Ehsan", "Kimia", "Kourosh", "Samar"};
    public static final String[] LAST_NAMES = {"Alipour", "Abbasi", "Amiri", "Baani", "Barzi", "Bahmani", "Salami", "Taheri", "Hemmati", "Afshar", "Salehi", "Karimi", "Samimi", "Nabavi", "Tehrani", "Tabatabaei", "Nosrati", "Bayat", "Barez", "Servati", "Bahar", "Farahani"};

    public static String randomFirstName() {
        int firstNameIndex = ThreadLocalRandom.current().nextInt(FIRST_NAMES.length);
        return FIRST_NAMES[firstNameIndex];
    }

    public static String randomLastName() {
        int lastNameIndex = ThreadLocalRandom.current().nextInt(LAST_NAMES.length);
        return LAST_NAMES[lastNameIndex];
    }
}
