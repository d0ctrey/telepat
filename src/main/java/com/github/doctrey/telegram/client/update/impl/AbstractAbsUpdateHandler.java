package com.github.doctrey.telegram.client.update.impl;

import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.update.AbsUpdateHandler;
import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.update.TLAbsUpdate;
import org.telegram.api.updates.TLAbsUpdates;

/**
 * Created by Soheil on 4/17/18.
 */
public abstract class AbstractAbsUpdateHandler<S extends TLAbsUpdates, T extends TLAbsUpdate> implements AbsUpdateHandler<S, T> {

    protected ListenerQueue listenerQueue;

    public AbstractAbsUpdateHandler(ListenerQueue listenerQueue) {
        this.listenerQueue = listenerQueue;
    }
}
