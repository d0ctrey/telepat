package com.github.doctrey.telegram.client.listener.event;

import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import org.telegram.api.engine.TelegramApi;

/**
 * Created by Soheil on 4/17/18.
 */
public class ChannelJoinedEvent extends AbstractEvent<ChannelSubscriptionInfo> {

    public ChannelJoinedEvent(ChannelSubscriptionInfo eventObject) {
        super(eventObject);
    }
}
