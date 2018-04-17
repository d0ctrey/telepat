package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.listener.event.Event;

/**
 * Created by Soheil on 12/28/17.
 */
public interface Listener<T extends Event> {

    Class<T> getEventClass();
    void inform(T event);
}
