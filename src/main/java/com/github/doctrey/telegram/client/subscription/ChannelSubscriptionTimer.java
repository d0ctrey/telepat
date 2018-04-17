package com.github.doctrey.telegram.client.subscription;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.facade.ChannelService;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TelegramApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private List<Integer> joinedChannels;

    public ChannelSubscriptionTimer(TelegramApi api, ListenerQueue listenerQueue) {
        this.api = api;
        timerThread = Executors.newSingleThreadScheduledExecutor();
        channelService = new ChannelService(listenerQueue, api);
        joinedChannels = new ArrayList<>();
        joinedChannels = channelService.findJoinedChannels(((DbApiStorage) api.getState()).getPhoneNumber());

    }

    public void startCheckingSubscriptions() {
        timerThread.scheduleAtFixedRate(() -> {
            List<ChannelSubscriptionInfo> allPendingChannels = channelService.findAllPendingChannels();
            allPendingChannels.forEach(channel -> {
                if (channel.getPlanExpiration().before(new Date())) {
                    if(joinedChannels.contains(channel.getId())) {
                        try {
                            channelService.leaveChannel(channel);
                        } catch (IOException | TimeoutException e) {

                        }
                        joinedChannels.remove(channel.getId());
                    }
                    // TODO s_tayari: @s_tayari 4/17/2018 mark this in another timer when all left
//                    channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.EXPIRED);

                } else if (channel.getMaxMember() > channel.getMemberCount() /*&& !joinedChannels.contains(channel.getId())*/) { // even if expired?
                    try {
                        channelService.joinChannel(channel);
                    } catch (IOException | TimeoutException e) {
                        if(e instanceof RpcException) {
                            if(((RpcException) e).getErrorTag().equals("INVITE_HASH_EXPIRED"))
                                channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.INACTIVE);
                            else if(((RpcException) e).getErrorTag().equals("USER_ALREADY_PARTICIPANT")) {
                                channelService.incrementMemberCount(channel.getId());
                                channelService.saveChannelMember(channel.getId(), ((DbApiStorage) api.getState()).getPhoneNumber());
                                joinedChannels.add(channel.getId());
                            }
                        }
                        return;
                    }
                    joinedChannels.add(channel.getId());

                } else if (channel.getMaxMember() <= channel.getMemberCount())
                    channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.VERIFIED_COMPLETE);
            });
        }, 5000, 1 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
