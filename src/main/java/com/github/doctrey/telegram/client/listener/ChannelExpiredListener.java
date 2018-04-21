package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.listener.event.ChannelExpiredEvent;
import org.telegram.api.engine.Logger;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by s_tayari on 4/19/2018.
 */
public class ChannelExpiredListener extends AbstractChannelListener<ChannelExpiredEvent> {

    private static final String TAG = "ChannelExpiredListener";

    public ChannelExpiredListener(ListenerQueue listenerQueue) {
        super(listenerQueue);
    }

    @Override
    public Class<ChannelExpiredEvent> getEventClass() {
        return ChannelExpiredEvent.class;
    }

    @Override
    public void inform(ChannelExpiredEvent event) {
        if(!channelListInitialized)
            initializedChannelList(event.getApi());

        Long accessHash = channelWhiteList.get(event.getEventObject());
        channelService.setApi(event.getApi());
        try {
            channelService.leaveChannel(event.getEventObject().getId(), event.getEventObject().getChannelId(), accessHash);
            channelService.decrementMember(event.getEventObject().getId());
            channelService.removeChannelMember(event.getEventObject().getId(), ((DbApiStorage) event.getApi().getState()).getPhoneNumber());
            channelWhiteList.remove(event.getEventObject());
        } catch (IOException | TimeoutException e) {
            Logger.e(TAG, e);
        }
    }
}
