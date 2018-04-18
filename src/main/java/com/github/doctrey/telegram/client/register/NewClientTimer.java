package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.RunnableApi;
import com.github.doctrey.telegram.client.listener.ClientJoinedListener;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.listener.event.ClientJoinedEvent;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.input.peer.TLInputPeerSelf;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Soheil on 4/12/18.
 */
public class NewClientTimer {

    private ScheduledExecutorService timerThread;
    private ExecutorService clientThreads;


    private RegistrationTimer registrationTimer;

    public NewClientTimer(RegistrationTimer registrationTimer) {
        this.registrationTimer = registrationTimer;
        timerThread = Executors.newSingleThreadScheduledExecutor();
        clientThreads = Executors.newFixedThreadPool(10);
    }

    public void checkForNewClients() {
        timerThread.scheduleAtFixedRate(() -> {
            if(!registrationTimer.isClientsUpdated())
                return;

            if(registrationTimer.isClientsUpdated()) {
                Map<String, TelegramApi> registeredApis = registrationTimer.getNewlyRegisteredApis();
                for(String number : registeredApis.keySet()) {
                    TelegramApi api = registeredApis.get(number);
                    ListenerQueue listenerQueue = new ListenerQueue();
                    listenerQueue.getListeners().add(new ClientJoinedListener(listenerQueue));

                    RunnableApi runnableApi = new RunnableApi(listenerQueue);
                    runnableApi.setApi(api);
                    clientThreads.submit(runnableApi);

                    listenerQueue.publish(new ClientJoinedEvent(new TLInputPeerSelf(), api));
                }
            }

        }, 5000, 2 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
