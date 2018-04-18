package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.listener.ClientJoinedListener;
import com.github.doctrey.telegram.client.listener.Listener;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.register.NewClientTimer;
import com.github.doctrey.telegram.client.register.PhoneNumberStatus;
import com.github.doctrey.telegram.client.register.RegistrationTimer;
import com.github.doctrey.telegram.client.register.VerificationTimer;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.engine.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class TelegramClient {

    private static final String TAG = "TelegramClient";

    // TODO: 2/16/18 read this from environment
    private static final int GROUP_ID = 1;

    public static void main(String[] args) {
        RegistrationTimer registrationTimer = new RegistrationTimer();
        registrationTimer.startCheckingForNewPhoneNumbers();

        VerificationTimer verificationTimer = new VerificationTimer(registrationTimer);
        verificationTimer.startVerifying();

        NewClientTimer newClientTimer = new NewClientTimer(registrationTimer);
        newClientTimer.checkForNewClients();

        // find active clients
        List<String> phoneNumbers = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT phone_number FROM tl_phone_numbers WHERE status = ?")){
            statement.setInt(1, PhoneNumberStatus.REGISTERED.getCode());
            try (ResultSet rs = statement.executeQuery()){
                while (rs.next()) {
                    phoneNumbers.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }

        List<Listener> listeners = new ArrayList<>();
        ListenerQueue listenerQueue = new ListenerQueue(listeners);

        // start clients
        ExecutorService clientThreads = Executors.newFixedThreadPool(10);
        for(String phone : phoneNumbers) {
            RunnableApi runnableApi = new RunnableApi(listenerQueue);
            runnableApi.setPhoneNumber(phone);
            clientThreads.submit(runnableApi);
        }

    }

    private static int generateRandomId() {
        return ThreadLocalRandom.current().nextInt(8, 16 + 1);
    }
}
