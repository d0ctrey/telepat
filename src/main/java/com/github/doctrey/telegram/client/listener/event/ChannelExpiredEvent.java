package com.github.doctrey.telegram.client.listener.event;

import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import org.telegram.api.engine.TelegramApi;

/**
 * Created by s_tayari on 4/19/2018.
 */
public class ChannelExpiredEvent extends AbstractEvent<ChannelSubscriptionInfo> {

    public ChannelExpiredEvent(ChannelSubscriptionInfo channel, TelegramApi api) {
        super(channel, api);
    }
}
