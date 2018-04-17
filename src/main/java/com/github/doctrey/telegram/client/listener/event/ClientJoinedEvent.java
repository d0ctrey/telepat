package com.github.doctrey.telegram.client.listener.event;

import org.telegram.api.input.peer.TLInputPeerSelf;

/**
 * Created by Soheil on 4/17/18.
 */
public class ClientJoinedEvent implements Event<TLInputPeerSelf> {

    private TLInputPeerSelf peerSelf;

    public ClientJoinedEvent(TLInputPeerSelf peerSelf) {
        this.peerSelf = peerSelf;
    }

    @Override
    public TLInputPeerSelf getTlObject() {
        return peerSelf;
    }
}
