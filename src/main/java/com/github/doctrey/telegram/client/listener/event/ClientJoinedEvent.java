package com.github.doctrey.telegram.client.listener.event;

import org.telegram.api.engine.TelegramApi;
import org.telegram.api.input.peer.TLInputPeerSelf;

/**
 * Created by Soheil on 4/17/18.
 */
public class ClientJoinedEvent extends AbstractEvent<TLInputPeerSelf> {

    public ClientJoinedEvent(TLInputPeerSelf eventObject) {
        super(eventObject);
    }
}
