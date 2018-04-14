package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.register.RegistrationTimer;
import org.telegram.api.engine.TelegramApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Soheil on 4/12/18.
 */
public class NewClientTimer {

    private ScheduledExecutorService timerThread;
    private RegistrationTimer registrationTimer;
    private List<TelegramApi> clients;

    public NewClientTimer(RegistrationTimer registrationTimer) {
        this.registrationTimer = registrationTimer;
        timerThread = Executors.newSingleThreadScheduledExecutor();
        clients = new ArrayList<>();
    }

    public void checkForNewClients() {
        timerThread.scheduleAtFixedRate(() -> {
            if(registrationTimer.isClientsUpdated())
                clients.addAll(registrationTimer.getNewClients());

        }, 5000, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
    }

    public List<TelegramApi> getClients() {
        return clients;
    }

    public void setClients(List<TelegramApi> clients) {
        this.clients = clients;
    }
}
