package com.github.doctrey.telegram.client.subscription;

import com.github.doctrey.telegram.client.facade.ChannelService;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.listener.event.ChannelExpiredEvent;
import org.telegram.api.engine.TelegramApi;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by s_tayari on 4/16/2018.
 */
public class ChannelExpirationTimer {

    private static final String TAG = "ChannelExpirationTimer";

    private ScheduledExecutorService timerThread;
    private TelegramApi api;
    private final ListenerQueue listenerQueue;
    private ChannelService channelService;

    public ChannelExpirationTimer(TelegramApi api, ListenerQueue listenerQueue) {
        this.api = api;
        this.listenerQueue = listenerQueue;
        timerThread = Executors.newSingleThreadScheduledExecutor();
        channelService = new ChannelService(listenerQueue);
        channelService.setApi(api);
    }

    public void startCheckingSubscriptions() {
        timerThread.scheduleAtFixedRate(() -> {
            List<ChannelSubscriptionInfo> allPendingChannels = channelService.findAllPendingChannels();
            allPendingChannels.forEach(channel -> {
                if (channel.getPlanExpiration().before(new Date()))
                    listenerQueue.publish(new ChannelExpiredEvent(channel, api));
            });
        }, 5000, 1 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
