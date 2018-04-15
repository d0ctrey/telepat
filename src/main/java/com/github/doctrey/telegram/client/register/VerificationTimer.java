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
 * Created by s_tayari on 4/12/2018.
 */
public class VerificationTimer {

    private static final String TAG = "VerificationTimer";

    private ScheduledExecutorService verificationTimerThreads;
    private ExecutorService verificationThreads;
    private Map<String, TelegramApi> registeredApis;
    private Map<String, TelegramApi> newlyRegisteredApis;
    private boolean allThreadsDone;

    public VerificationTimer(RegistrationTimer registrationTimer) {
        this.registeredApis = registrationTimer.getRegisteredApis();
        this.newlyRegisteredApis = registrationTimer.getNewlyRegisteredApis();
        verificationTimerThreads = Executors.newSingleThreadScheduledExecutor();
        verificationThreads = Executors.newFixedThreadPool(10);
    }

    public void startVerifying() {
        verificationTimerThreads.scheduleAtFixedRate(() -> {
            Map<String, String> phoneNumberToCodeMap = new HashMap<>();
            try (Connection connection = ConnectionPool.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT phone_number, security_code FROM tl_phone_numbers WHERE status = ?")){
                statement.setInt(1, PhoneNumberStatus.CODE_RECEIVED.getCode());
                try (ResultSet rs = statement.executeQuery()){
                    while (rs.next()) {
                        phoneNumberToCodeMap.put(rs.getString(1), rs.getString(2));
                    }
                }
            } catch (SQLException e) {
                Logger.e(TAG, e);
            }

            if(phoneNumberToCodeMap.isEmpty()) {
                allThreadsDone = true;
                return;
            }

            // start verifying numbers
            List<Future> futureList = new ArrayList<>();
            for(String number : phoneNumberToCodeMap.keySet()) {
                VerificationRunnable runnable = new VerificationRunnable();
                if(registeredApis.containsKey(number)) {
                    runnable.setApi(registeredApis.get(number));
                } else {
                    runnable.setPhoneNumber(number);
                }
                Future future = verificationThreads.submit(runnable);
                futureList.add(future);
            }

            while (!allThreadsDone(futureList)) {

            }

            this.allThreadsDone = true;

        }, 50000, 1 * 30 * 1000, TimeUnit.MILLISECONDS);
    }


    public void stopVerifying() {
        verificationTimerThreads.shutdown();
    }

    @SuppressWarnings("Duplicates")
    private boolean allThreadsDone(List<Future> futureList) {
        for(Future<TelegramApi> future : futureList){
            if(future.isDone())
                try {
                    TelegramApi api = future.get();
                    registeredApis.put(((DbApiStorage) api.getState()).getPhoneNumber(), api);
                    newlyRegisteredApis.put(((DbApiStorage) api.getState()).getPhoneNumber(), api);
                } catch (InterruptedException | ExecutionException e) {
                    Logger.e(TAG, e);
                }
            else
                return false;
        }

        return true;
    }

    public boolean isAllThreadsDone() {
        return allThreadsDone;
    }
}
