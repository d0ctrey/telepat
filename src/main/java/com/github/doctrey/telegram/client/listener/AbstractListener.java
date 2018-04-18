package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.listener.event.Event;

/**
 * Created by s_tayari on 4/18/2018.
 */
public abstract class AbstractListener<T extends Event> implements Listener<T> {

    protected ListenerQueue listenerQueue;

    public AbstractListener(ListenerQueue listenerQueue) {
        this.listenerQueue = listenerQueue;
    }
}
