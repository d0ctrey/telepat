package com.github.doctrey.telegram.client.listener.event;

import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import org.telegram.api.input.peer.TLInputPeerSelf;

/**
 * Created by Soheil on 4/17/18.
 */
public class ChannelJoinedEvent implements Event<ChannelSubscriptionInfo> {

    private ChannelSubscriptionInfo channelSubscriptionInfo;

    public ChannelJoinedEvent(ChannelSubscriptionInfo channelSubscriptionInfo) {
        this.channelSubscriptionInfo = channelSubscriptionInfo;
    }

    @Override
    public ChannelSubscriptionInfo getTlObject() {
        return channelSubscriptionInfo;
    }
}
