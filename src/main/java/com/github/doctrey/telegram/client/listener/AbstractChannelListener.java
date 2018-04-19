package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.facade.ChannelService;
import com.github.doctrey.telegram.client.listener.event.Event;
import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import org.telegram.api.engine.TelegramApi;

import java.util.Map;

/**
 * Created by s_tayari on 4/19/2018.
 */
public abstract class AbstractChannelListener<T extends Event> extends AbstractListener<T> {

    protected final ChannelService channelService;
    protected boolean channelListInitialized;
    protected Map<ChannelSubscriptionInfo, Long> channelWhiteList;

    public AbstractChannelListener(ListenerQueue listenerQueue) {
        super(listenerQueue);
        channelService = new ChannelService(listenerQueue);
    }

    protected void initializedChannelList(TelegramApi api) {
        Map<Integer, Long> joinedChannels = channelService.findJoinedChannels(((DbApiStorage) api.getState()).getPhoneNumber());
        joinedChannels.forEach((id, hash) -> channelWhiteList.put(channelService.findChannel(id), hash));
        channelListInitialized = true;
    }
}
