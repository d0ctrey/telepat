package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.register.RegistrationService;

import java.sql.Connection;
import java.util.concurrent.ThreadLocalRandom;

public class TelegramClient {

    // TODO: 2/16/18 read this from environment
    private static final int GROUP_ID = 1;

    public static void main(String[] args) {
        Connection connection = null;
        RegistrationService registrationService = new RegistrationService();
        registrationService.startCheckingForNewPhoneNumbers();

        // start a schedule to check for new inactive users

    }

    private static int generateRandomId() {
        return ThreadLocalRandom.current().nextInt(8, 16 + 1);
    }
}
