package com.github.doctrey.telegram.client.listener.event;

import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.input.peer.TLInputPeerChannel;

/**
 * Created by Soheil on 4/17/18.
 */
public class MessageViewedEvent implements Event<TLInputPeerChannel> {

    private TLInputPeerChannel peerChannel;

    public MessageViewedEvent(TLInputPeerChannel peerChannel) {
        this.peerChannel = peerChannel;
    }

    @Override
    public TLInputPeerChannel getTlObject() {
        return peerChannel;
    }
}
