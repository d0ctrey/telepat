package com.github.doctrey.telegram.client.listener.event;

import org.telegram.api.engine.TelegramApi;
import org.telegram.api.input.peer.TLInputPeerChannel;

/**
 * Created by Soheil on 4/17/18.
 */
public class MessageViewedEvent extends AbstractEvent<TLInputPeerChannel> {

    public MessageViewedEvent(TLInputPeerChannel eventObject, TelegramApi api) {
        super(eventObject, api);
    }
}
