package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.engine.Logger;

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
    private boolean allThreadsDone;

    public VerificationTimer() {
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

            // start verifying numbers
            List<Future> futureList = new ArrayList<>();
            for(String number : phoneNumberToCodeMap.keySet()) {
                VerificationRunnable runnable = new VerificationRunnable();
                runnable.setPhoneNumber(number);
                runnable.setSecurityCode(phoneNumberToCodeMap.get(number));
                Future future = verificationThreads.submit(runnable);
                futureList.add(future);
            }

            while (!allThreadsDone(futureList)) {

            }

            this.allThreadsDone = true;

        }, 5000, 1 * 60 * 1000, TimeUnit.MILLISECONDS);
    }


    public void stopVerifying() {
        verificationTimerThreads.shutdown();
    }

    private boolean allThreadsDone(List<Future> futureList) {
        boolean allDone = true;
        for(Future<?> future : futureList){
            allDone &= future.isDone();
        }

        return allDone;
    }

    public boolean isAllThreadsDone() {
        return allThreadsDone;
    }
}
