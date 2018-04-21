package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.listener.event.EventType;
import com.github.doctrey.telegram.client.listener.event.NewChannelToJoinEvent;
import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import com.github.doctrey.telegram.client.util.MessageUtils;
import org.telegram.api.engine.Logger;
import org.telegram.api.functions.messages.TLRequestMessagesSendMessage;
import org.telegram.api.input.peer.TLInputPeerChat;
import org.telegram.api.updates.TLAbsUpdates;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Soheil on 4/16/18.
 */
public class ChannelJoinListener extends AbstractChannelListener<NewChannelToJoinEvent> {

    private static final String TAG = "ChannelJoinListener";

    private int groupIdToPost;

    public ChannelJoinListener(ListenerQueue listenerQueue) {
        super(listenerQueue);

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT group_id FROM tl_admin_groups WHERE group_type = ?")) {
            statement.setInt(1, EventType.JOIN_CHANNEL.getCode());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    groupIdToPost = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }


    @Override
    public Class<NewChannelToJoinEvent> getEventClass() {
        return NewChannelToJoinEvent.class;
    }

    @Override
    public void inform(NewChannelToJoinEvent event) {
        if(!channelListInitialized)
            initializedChannelList(event.getApi());

        assert groupIdToPost != 0;

        channelService.setApi(event.getApi());
        try {
            Map<ChannelSubscriptionInfo, Long> joinedChannel = channelService.joinChannel(event.getEventObject());
            channelService.saveChannelMember(event.getEventObject().getId(), ((DbApiStorage) event.getApi().getState()).getPhoneNumber(), joinedChannel.get(event.getEventObject()));
            channelService.incrementMemberCount(event.getEventObject().getId());
            channelService.updateChannelId(event.getEventObject().getId(), event.getEventObject().getChannelId());
            channelWhiteList.putAll(joinedChannel);
        } catch (IOException | TimeoutException e) {
            Logger.e(TAG, e);
            return;
        }

        TLInputPeerChat inputPeerChat = new TLInputPeerChat();
        inputPeerChat.setChatId(groupIdToPost);
        TLRequestMessagesSendMessage sendMessage = new TLRequestMessagesSendMessage();
        sendMessage.setRandomId(MessageUtils.generateRandomId());
        sendMessage.setMessage("Joined channel " + event.getEventObject().getChannelId() + ".");
        sendMessage.setPeer(inputPeerChat);

        event.getApi().doRpcCall(sendMessage, new AbstractRpcCallback<TLAbsUpdates>() {
            @Override
            public void onResult(TLAbsUpdates result) {

            }
        });
    }
}
