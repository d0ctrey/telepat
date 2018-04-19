package com.github.doctrey.telegram.client.subscription;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.facade.ChannelService;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import org.telegram.api.engine.TelegramApi;
import org.telegram.bot.kernel.engine.MemoryApiState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by s_tayari on 4/19/2018.
 */
public class ChannelSubTimer {

    private static final String TAG = "ChannelSubTimer";

    private ScheduledExecutorService timerThread;
    private final ListenerQueue listenerQueue;
    private ChannelService channelService;
    private List<TelegramApi> clients;
    private Map<Integer, List<TelegramApi>> channelSubscriptions;

    public ChannelSubTimer(List<TelegramApi> clients, ListenerQueue listenerQueue) {
        this.clients = clients;
        this.listenerQueue = listenerQueue;
        channelSubscriptions = new HashMap<>();
        timerThread = Executors.newSingleThreadScheduledExecutor();
        channelService = new ChannelService(listenerQueue);
    }

    public void startCheckingSubscriptions() {
        timerThread.scheduleAtFixedRate(() -> {
            List<ChannelSubscriptionInfo> allPendingChannels = channelService.findAllPendingChannels();
            allPendingChannels.forEach(channel -> {
                int memberCount = channel.getMemberCount();
                int maxMember = channel.getMaxMember();

                if(!channelSubscriptions.containsKey(channel.getId()))
                    channelSubscriptions.put(channel.getId(), new ArrayList<>());

                if (maxMember > memberCount) { // even if expired?
                    clients.stream().filter(api -> channelSubscriptions.get(channel.getId()).stream()
                            .map(api2 -> ((DbApiStorage) api2.getState()).getPhoneNumber())
                            .anyMatch(phoneNumber -> phoneNumber.equals(((DbApiStorage) api.getState()).getPhoneNumber())));
                    if(channelSubscriptions.get(channel.getId()).stream()
                            .noneMatch(api -> clients.stream()
                                    .map(api2 -> ((DbApiStorage) api2.getState()).getPhoneNumber())
                                    .anyMatch(phoneNumber -> phoneNumber.equals(((DbApiStorage) api.getState()).getPhoneNumber()))))


                } else if (channel.getMaxMember() <= channel.getMemberCount())
                    channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.VERIFIED_COMPLETE);
            });
        }, 5000, 1 * 60 * 1000, TimeUnit.MILLISECONDS);
    }


}
