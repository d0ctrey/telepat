package com.github.doctrey.telegram.client.listener.event;

import org.telegram.api.engine.TelegramApi;

/**
 * Created by s_tayari on 4/17/2018.
 */
public abstract class AbstractEvent<T> implements Event<T> {

    protected T eventObject;
    protected TelegramApi api;

    public AbstractEvent(T eventObject, TelegramApi api) {
        this.eventObject = eventObject;
        this.api = api;
    }

    @Override
    public T getEventObject() {
        return eventObject;
    }

    @Override
    public TelegramApi getApi() {
        return api;
    }
}
