package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.engine.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by s_tayari on 4/11/2018.
 */
public class RegistrationTimer {

    private static final String TAG = "RegistrationTimer";

    private ScheduledExecutorService registrationTimerThreads;
    private ExecutorService registrationThreads;


    public RegistrationTimer() {
        registrationTimerThreads = Executors.newSingleThreadScheduledExecutor();
        registrationThreads = Executors.newFixedThreadPool(10);
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

            // start registering numbers
            List<Future> futureList = new ArrayList<>();
            for(String number : phoneNumbers) {
                RegistrationRunnable runnable = new RegistrationRunnable();
                runnable.setPhoneNumber(number);
                Future future = registrationThreads.submit(runnable);
                futureList.add(future);
            }

            while (!allThreadsDone(futureList)) {
                // just continue the loop
            }

            VerificationTimer verificationTimer = new VerificationTimer();
            verificationTimer.startVerifying();

            while (!verificationTimer.isAllThreadsDone()) {
                // just continue the loop
            }

            verificationTimer.stopVerifying();

        }, 5000, 1 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    private boolean allThreadsDone(List<Future> futureList) {
        boolean allDone = true;
        for(Future<?> future : futureList){
            allDone &= future.isDone();
        }

        return allDone;
    }
}
