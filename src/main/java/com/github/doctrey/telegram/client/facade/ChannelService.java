package com.github.doctrey.telegram.client.facade;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.listener.event.ChannelJoinedEvent;
import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionStatus;
import com.github.doctrey.telegram.client.subscription.ChannelType;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.contacts.TLResolvedPeer;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.channels.TLRequestChannelsJoinChannel;
import org.telegram.api.functions.channels.TLRequestChannelsLeaveChannel;
import org.telegram.api.functions.contacts.TLRequestContactsResolveUsername;
import org.telegram.api.functions.messages.TLRequestMessagesImportChatInvite;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.api.updates.TLUpdates;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by s_tayari on 4/16/2018.
 */
public class ChannelService {

    private static final String TAG = "ChannelService";
    private ListenerQueue listenerQueue;
    private TelegramApi api;

    public ChannelService(ListenerQueue listenerQueue, TelegramApi api) {
        this.listenerQueue = listenerQueue;
        this.api = api;
    }

    public ChannelService(ListenerQueue listenerQueue) {
        this.listenerQueue = listenerQueue;
    }

    public ChannelService() {
    }

    public void markChannel(int id, ChannelSubscriptionStatus status) {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE tl_channels SET status = ? WHERE id = ?")
        ) {
            statement.setInt(1, status.getCode());
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    public void updateChannelIdAndHash(ChannelSubscriptionInfo channel) {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE tl_channels SET channel_id = ?, access_hash = ? WHERE id = ?")
        ) {
            statement.setInt(1, channel.getChannelId());
            statement.setLong(2, channel.getAccessHash());
            statement.setInt(3, channel.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    public List<Integer> findJoinedChannels(String phoneNumber) {
        List<Integer> joinedChannels = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT channels_id FROM tl_channel_members WHERE phone_number = ?")) {
            statement.setString(1, phoneNumber);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    joinedChannels.add(rs.getInt(1));
                }
            }

            return joinedChannels;
        } catch (SQLException e) {
            Logger.e(TAG, e);
            return Collections.emptyList();
        }
    }

    public List<ChannelSubscriptionInfo> findAllPendingChannels() {
        List<ChannelSubscriptionInfo> subscriptionInfoList = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM tl_channels WHERE status = ?")) {
            statement.setInt(1, ChannelSubscriptionStatus.VERIFIED.getCode());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ChannelSubscriptionInfo info = new ChannelSubscriptionInfo();
                    info.setId(rs.getInt(1));
                    info.setInviteLink(rs.getString(2));
                    info.setChannelId(rs.getInt(3));
                    info.setPlanId(rs.getInt(4));
                    info.setPlanStart(rs.getDate(5));
                    info.setPlanExpiration(rs.getDate(6));
                    info.setMemberCount(rs.getInt(7));
                    info.setSubscriptionStatus(ChannelSubscriptionStatus.withCode(rs.getInt(8)));
                    info.setMaxMember(rs.getInt(9));
                    info.setPublicLink(rs.getString(10));
                    info.setChannelType(ChannelType.withCode(rs.getInt(11)));
                    info.setAccessHash(rs.getLong(12));
                    subscriptionInfoList.add(info);
                }
            }

            return subscriptionInfoList;
        } catch (SQLException e) {
            Logger.e(TAG, e);
            return Collections.emptyList();
        }
    }

    public void incrementMemberCount(int channelId) {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE tl_channels SET member_count = member_count + 1 WHERE id = ?")
        ) {
            statement.setInt(1, channelId);
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    private void decrementMember(int channelId) {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE tl_channels SET member_count = member_count - 1 WHERE id = ?")
        ) {
            statement.setInt(1, channelId);
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    public void saveChannelMember(int channelId, String phoneNumber) {
        try (
                Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement("INSERT INTO tl_channel_members VALUES (?, ?)")
        ) {
            statement.setInt(1, channelId);
            statement.setString(2, phoneNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    private void removeChannelMember(int channelId, String phoneNumber) {
        try (
                Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement("DELETE tl_channel_members VALUES (?, ?)")
        ) {
            statement.setInt(1, channelId);
            statement.setString(2, phoneNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    public void joinChannel(ChannelSubscriptionInfo channel) throws IOException, TimeoutException {
        long hash;
        int channelId;
        if (channel.getChannelType().equals(ChannelType.PRIVATE)) {
            String link = channel.getInviteLink();
            if (link.startsWith("https://"))
                link = link.substring(8);
            String[] linkParts = link.split("/");

            TLRequestMessagesImportChatInvite importChatInvite = new TLRequestMessagesImportChatInvite();
            importChatInvite.setHash(linkParts[linkParts.length - 1]);
            TLAbsUpdates tlAbsUpdates;
            try {
                tlAbsUpdates = api.doRpcCall(importChatInvite);
                TLChannel tlChannel = ((TLChannel) ((TLUpdates) tlAbsUpdates).getChats().get(0));
                hash = tlChannel.getAccessHash();
                channelId = tlChannel.getId();
            } catch (Exception e) {
                Logger.e(TAG, e);
                throw e;
            }
        } else {
            String link = channel.getPublicLink();
            if (link.startsWith("https://"))
                link = link.substring(8);
            String[] linkParts = link.split("/");
            try {
                TLRequestContactsResolveUsername resolveUsername = new TLRequestContactsResolveUsername();
                resolveUsername.setUsername(linkParts[linkParts.length - 1]);
                TLResolvedPeer peer = api.doRpcCall(resolveUsername);
                TLRequestChannelsJoinChannel joinChannel = new TLRequestChannelsJoinChannel();
                TLInputChannel inputChannel = new TLInputChannel();
                inputChannel.setChannelId(peer.getChats().get(0).getId());
                inputChannel.setAccessHash(((TLChannel) peer.getChats().get(0)).getAccessHash());
                joinChannel.setChannel(inputChannel);
                TLAbsUpdates tlAbsUpdates = api.doRpcCall(joinChannel);
                TLChannel tlChannel = ((TLChannel) ((TLUpdates) tlAbsUpdates).getChats().get(0));
                hash = tlChannel.getAccessHash();
                channelId = tlChannel.getId();
            } catch (Exception e) {
                Logger.e(TAG, e);
                throw e;
            }
        }

        channel.setChannelId(channelId);
        channel.setAccessHash(hash);

        incrementMemberCount(channel.getId());
        saveChannelMember(channel.getId(), ((DbApiStorage) api.getState()).getPhoneNumber());
        listenerQueue.publish(new ChannelJoinedEvent(channel, api));
    }

    public void leaveChannel(ChannelSubscriptionInfo channel) throws IOException, TimeoutException {
        TLInputChannel inputChannel = new TLInputChannel();
        inputChannel.setChannelId(channel.getChannelId());
        inputChannel.setAccessHash(channel.getAccessHash());
        TLRequestChannelsLeaveChannel leaveChannel = new TLRequestChannelsLeaveChannel();
        leaveChannel.setChannel(inputChannel);
        try {
            api.doRpcCall(leaveChannel);
        } catch (IOException | TimeoutException e) {
            Logger.e(TAG, e);
            throw e;
        }
        removeChannelMember(channel.getId(), ((DbApiStorage) api.getState()).getPhoneNumber());
        decrementMember(channel.getId());
        // TODO s_tayari: @s_tayari 4/17/2018 raise event here?
    }


    public void setApi(TelegramApi api) {
        this.api = api;
    }
}
