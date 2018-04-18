package com.github.doctrey.telegram.client.listener.event;

import org.telegram.api.engine.TelegramApi;

/**
 * Created by Soheil on 4/17/18.
 */
public interface Event<T> {

    T getEventObject();
}
