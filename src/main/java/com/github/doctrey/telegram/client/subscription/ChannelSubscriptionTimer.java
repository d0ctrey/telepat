package com.github.doctrey.telegram.client.subscription;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.facade.ChannelService;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import org.telegram.api.engine.TelegramApi;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by s_tayari on 4/16/2018.
 */
public class ChannelSubscriptionTimer {

    private static final String TAG = "ChannelSubscriptionTimer";

    private ScheduledExecutorService timerThread;
    private TelegramApi api;
    private ChannelService channelService;
    private Map<Integer, Long> joinedChannels;

    public ChannelSubscriptionTimer(TelegramApi api, ListenerQueue listenerQueue) {
        this.api = api;
        timerThread = Executors.newSingleThreadScheduledExecutor();
        channelService = new ChannelService(listenerQueue);
        channelService.setApi(api);
        joinedChannels = channelService.findJoinedChannels(((DbApiStorage) api.getState()).getPhoneNumber());
    }

    public void startCheckingSubscriptions() {
        timerThread.scheduleAtFixedRate(() -> {
            List<ChannelSubscriptionInfo> allPendingChannels = channelService.findAllPendingChannels();
            allPendingChannels.forEach(channel -> {
                if (channel.getPlanExpiration().before(new Date()) && joinedChannels.containsKey(channel.getId())) {

                    try {
                        channelService.leaveChannel(channel.getId(), channel.getChannelId(), joinedChannels.get(channel.getId()));
                        joinedChannels.remove(channel.getId());
                    } catch (IOException | TimeoutException e) {
                        channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.ERROR);
                    }

                } else if (channel.getMaxMember() > channel.getMemberCount() && !joinedChannels.containsKey(channel.getId())) { // even if expired?
                    try {
                        long hash = channelService.joinChannel(channel);
                        joinedChannels.put(channel.getId(), hash);
                    } catch (IOException | TimeoutException e) {
                        channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.ERROR);
                    }
                } else if (channel.getMaxMember() <= channel.getMemberCount())
                    channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.VERIFIED_COMPLETE);
            });
        }, 5000, 1 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
