package com.github.doctrey.telegram.client.subscription;

import com.github.doctrey.telegram.client.ApiStorage;
import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.facade.ChannelService;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.listener.event.NewChannelToJoinEvent;
import org.telegram.api.engine.TelegramApi;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by s_tayari on 4/19/2018.
 */
public class ChannelSubscriptionTimer {

    private static final String TAG = "ChannelSubscriptionTimer";

    private ScheduledExecutorService timerThread;
    private final ListenerQueue listenerQueue;
    private ChannelService channelService;
    private List<TelegramApi> clients;
    private Map<Integer, List<String>> channelSubscriptions;

    public ChannelSubscriptionTimer(List<TelegramApi> clients, ListenerQueue listenerQueue) {
        this.clients = clients;
        this.listenerQueue = listenerQueue;
        timerThread = Executors.newSingleThreadScheduledExecutor();
        channelService = new ChannelService(listenerQueue);
        channelSubscriptions = channelService.findJoinedChannels();
    }

    public void startCheckingNewSubscriptions() {
        timerThread.scheduleAtFixedRate(() -> {
            List<ChannelSubscriptionInfo> allPendingChannels = channelService.findByStatus(ChannelSubscriptionStatus.VERIFIED);
            allPendingChannels.forEach(channel -> {
                if (channel.getPlanExpiration().before(new Date())) {
                    channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.EXPIRED);
                    channelSubscriptions.remove(channel.getId());
                }

                int memberCount = channel.getMemberCount();
                int maxMember = channel.getMaxMember();
                boolean completed = false;

                if(!channelSubscriptions.containsKey(channel.getId()))
                    channelSubscriptions.put(channel.getId(), new ArrayList<>());

                if (maxMember > memberCount) { // even if expired?
                    for(TelegramApi api : clients) {
                        if(memberCount >= maxMember) {
                            break;
                        }

                        if(channelSubscriptions.get(channel.getId()).stream()
                                .noneMatch(phoneNumber -> phoneNumber.equals(((DbApiStorage) api.getState()).getPhoneNumber()))) {
                            listenerQueue.publish(new NewChannelToJoinEvent(channel, api));
                            channelSubscriptions.get(channel.getId()).add(((DbApiStorage) api.getState()).getPhoneNumber());
                            memberCount++;
                        }
                    }
                }

                if(memberCount >= maxMember) {
                    channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.VERIFIED_COMPLETE);
                }

//                channel.setMemberCount(memberCount);
//                channelService.updateMemberCount(channel.getId(), memberCount);

            });
        }, 5000, 1 * 60 * 1000, TimeUnit.MILLISECONDS);
    }


}
