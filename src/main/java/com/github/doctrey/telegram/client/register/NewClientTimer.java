package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.listener.event.ClientJoinedEvent;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.input.peer.TLInputPeerSelf;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Soheil on 4/12/18.
 */
public class NewClientTimer {

    private ScheduledExecutorService timerThread;

    private RegistrationTimer registrationTimer;
    private ListenerQueue listenerQueue;

    public NewClientTimer(RegistrationTimer registrationTimer, ListenerQueue listenerQueue) {
        this.registrationTimer = registrationTimer;
        this.listenerQueue = listenerQueue;
        timerThread = Executors.newSingleThreadScheduledExecutor();
    }

    public void checkForNewClients() {
        timerThread.scheduleAtFixedRate(() -> {
            if(!registrationTimer.isClientsUpdated())
                return;

            if(registrationTimer.isClientsUpdated()) {
                Map<String, TelegramApi> registeredApis = registrationTimer.getNewlyRegisteredApis();
                for(String number : registeredApis.keySet()) {
                    listenerQueue.publish(new ClientJoinedEvent(new TLInputPeerSelf(), registeredApis.get(number)));
                }
            }

        }, 5000, 2 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
