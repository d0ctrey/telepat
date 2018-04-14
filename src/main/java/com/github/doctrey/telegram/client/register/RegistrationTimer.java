package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by s_tayari on 4/11/2018.
 */
public class RegistrationTimer {

    private static final String TAG = "RegistrationTimer";

    private ScheduledExecutorService registrationTimerThreads;
    private ExecutorService registrationThreads;
    private Map<String, TelegramApi> newClients;
    private boolean clientsUpdated;


    public RegistrationTimer() {
        registrationTimerThreads = Executors.newSingleThreadScheduledExecutor();
        registrationThreads = Executors.newFixedThreadPool(10);
        newClients = new HashMap<>();
    }

    public void startCheckingForNewPhoneNumbers() {
        registrationTimerThreads.scheduleAtFixedRate(() -> {
            List<String> phoneNumbers = new ArrayList<>();
            try (Connection connection = ConnectionPool.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT phone_number FROM tl_phone_numbers WHERE status = ?")){
                statement.setInt(1, PhoneNumberStatus.NEW.getCode());
                try (ResultSet rs = statement.executeQuery()){
                    while (rs.next()) {
                        phoneNumbers.add(rs.getString(1));
                    }
                }
            } catch (SQLException e) {
                Logger.e(TAG, e);
            }

            if(phoneNumbers.isEmpty())
                return;

            // start registering numbers
            List<Future<TelegramApi>> futureList = new ArrayList<>();
            for(String number : phoneNumbers) {
                RegistrationCallable runnable = new RegistrationCallable();
                if(newClients.containsKey(number))
                    runnable.setApi(newClients.get(number));
                else
                    runnable.setPhoneNumber(number);
                Future<TelegramApi> future = registrationThreads.submit(runnable);
                futureList.add(future);
            }

            // TODO: 4/12/18 replace with shutdown maybe?
            while (!allThreadsDone(futureList)) {
                // just continue the loop
            }

            // start verifying after all codes are sent
            VerificationTimer verificationTimer = new VerificationTimer();
            verificationTimer.setNewClients(newClients);
            verificationTimer.startVerifying();

            while (!verificationTimer.isAllThreadsDone()) {
                // just continue the loop
            }

            verificationTimer.stopVerifying();
            clientsUpdated = true;

        }, 5000, 1 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    private boolean allThreadsDone(List<Future<TelegramApi>> futureList) {
        for(Future<TelegramApi> future : futureList){
            if(future.isDone())
                try {
                    TelegramApi api = future.get();
                    newClients.put(((DbApiStorage) api.getState()).getPhoneNumber(), api);
                } catch (InterruptedException | ExecutionException e) {
                    Logger.e(TAG, e);
                }
            else
                return false;
        }

        return true;
    }

    public boolean isClientsUpdated() {
        return clientsUpdated;
    }

    public List<TelegramApi> getNewClients() {
        clientsUpdated = false;
        List<TelegramApi> apiList = new ArrayList<>(newClients.values());
        newClients.clear();
        return apiList;
    }
}
