package com.github.doctrey.telegram.client.subscription;

import java.util.Date;

/**
 * Created by s_tayari on 4/16/2018.
 */
public class ChannelSubscriptionInfo {

    private int id;
    private String inviteLink;
    private int channelId;
    private int planId;
    private Date planStart;
    private Date planExpiration;
    private int memberCount;
    private ChannelSubscriptionStatus subscriptionStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public Date getPlanStart() {
        return planStart;
    }

    public void setPlanStart(Date planStart) {
        this.planStart = planStart;
    }

    public Date getPlanExpiration() {
        return planExpiration;
    }

    public void setPlanExpiration(Date planExpiration) {
        this.planExpiration = planExpiration;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public ChannelSubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(ChannelSubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
}
