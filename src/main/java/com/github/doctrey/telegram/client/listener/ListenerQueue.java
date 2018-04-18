package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.listener.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s_tayari on 4/17/2018.
 */
public class ListenerQueue {

    private List<Listener> listeners;

    public ListenerQueue() {
        listeners = new ArrayList<>();
    }

    public ListenerQueue(List<Listener> listeners) {
        this.listeners = listeners;
    }

    public void publish(Event event) {
        listeners.forEach(listener -> {
            if(listener.getEventClass().equals(event.getClass()))
                listener.inform(event);
        });
    }

    public List<Listener> getListeners() {
        return listeners;
    }
}
