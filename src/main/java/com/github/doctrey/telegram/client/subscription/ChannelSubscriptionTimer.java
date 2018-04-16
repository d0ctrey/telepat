package com.github.doctrey.telegram.client.subscription;

import com.github.doctrey.telegram.client.facade.ChannelService;
import org.telegram.api.engine.TelegramApi;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by s_tayari on 4/16/2018.
 */
public class ChannelSubscriptionTimer {

    private static final String TAG = "ChannelSubscriptionTimer";

    private ScheduledExecutorService timerThread;
    private TelegramApi api;
    private ChannelService channelService;

    public ChannelSubscriptionTimer(TelegramApi api) {
        this.api = api;
        timerThread = Executors.newSingleThreadScheduledExecutor();
        channelService = new ChannelService(api);
    }

    public void startCheckingSubscriptions() {
        timerThread.scheduleAtFixedRate(() -> {
            List<ChannelSubscriptionInfo> allPendingChannels = channelService.findAllPendingChannels();
            allPendingChannels.forEach(channel -> {
                SubscriptionPlan plan = channelService.findPlanForChannel(channel.getId());
                if(plan.getRequiredMember() > channel.getMemberCount()) { // even if expired?
                    channelService.joinChannel(channel.getId(), channel.getInviteLink());

                } else if(channel.getPlanExpiration().before(new Date()))
                    channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.EXPIRED);

                else if(plan.getRequiredMember() <= channel.getMemberCount())
                    channelService.markChannel(channel.getId(), ChannelSubscriptionStatus.VERIFIED_COMPLETE);
            });
        }, 5000, 5 * 60 * 1000, TimeUnit.MILLISECONDS);
    }
}
