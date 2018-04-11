package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.RunnableApi;
import com.github.doctrey.telegram.client.util.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by s_tayari on 4/11/2018.
 */
public class RegistrationService {

    private ScheduledExecutorService scheduledExecutorService;
    ExecutorService executorService;


    public RegistrationService() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        executorService = Executors.newFixedThreadPool(100);
    }

    public void startCheckingForNewPhoneNumbers() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            List<String> phoneNumbers = new ArrayList<>();
            try (Connection connection = ConnectionPool.getInstance().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM tl_phone_numbers WHERE status = ?")){
                statement.setInt(1, PhoneNumberStatus.NEW.getCode());
                try (ResultSet rs = statement.executeQuery()){
                    while (rs.next()) {
                        phoneNumbers.add(rs.getString(1));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // start registering numbers
            for(String number : phoneNumbers) {
                RunnableApi runnableApi = new RunnableApi();
                runnableApi.setPhoneNumber(number);
                executorService.submit(runnableApi);
            }

        }, 5000, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
