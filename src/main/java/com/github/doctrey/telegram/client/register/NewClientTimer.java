package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.RunnableApi;
import com.github.doctrey.telegram.client.listener.ClientJoinedListener;
import com.github.doctrey.telegram.client.listener.Listener;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.listener.event.ClientJoinedEvent;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.input.peer.TLInputPeerSelf;

import java.util.ArrayList;
import java.util.List;
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
                    List<Listener> listeners = new ArrayList<>();
                    listeners.add(new ClientJoinedListener(api));
                    ListenerQueue listenerQueue = new ListenerQueue(listeners);

                    RunnableApi runnableApi = new RunnableApi();
                    runnableApi.setApi(api);
                    runnableApi.setListenerQueue(listenerQueue);
                    clientThreads.submit(runnableApi);

                    listenerQueue.publish(new ClientJoinedEvent(new TLInputPeerSelf()));
                }
            }

        }, 5000, 2 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
