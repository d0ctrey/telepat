package com.github.doctrey.telegram.client.listener.event;

import org.telegram.api.engine.TelegramApi;

/**
 * Created by s_tayari on 4/17/2018.
 */
public abstract class AbstractEvent<T> implements Event<T> {

    protected T eventObject;

    public AbstractEvent(T eventObject) {
        this.eventObject = eventObject;
    }

    @Override
    public T getEventObject() {
        return eventObject;
    }

}
