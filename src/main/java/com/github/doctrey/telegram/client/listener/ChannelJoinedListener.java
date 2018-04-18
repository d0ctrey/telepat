package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.facade.ChannelService;
import com.github.doctrey.telegram.client.listener.event.ChannelJoinedEvent;
import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import com.github.doctrey.telegram.client.util.MessageUtils;
import org.telegram.api.engine.Logger;
import org.telegram.api.functions.messages.TLRequestMessagesSendMessage;
import org.telegram.api.input.peer.TLInputPeerChat;
import org.telegram.api.updates.TLAbsUpdates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Soheil on 4/16/18.
 */
public class ChannelJoinedListener extends AbstractListener<ChannelJoinedEvent> {

    private static final String TAG = "ChannelJoinedListener";

    private int groupIdToPost;
    private List<ChannelSubscriptionInfo> joinedChannels;
    private ChannelService channelService;

    public ChannelJoinedListener(ListenerQueue listenerQueue) {
        super(listenerQueue);
        this.joinedChannels = new ArrayList<>();
        channelService = new ChannelService(listenerQueue);

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
    public Class<ChannelJoinedEvent> getEventClass() {
        return ChannelJoinedEvent.class;
    }

    @Override
    public void inform(ChannelJoinedEvent event) {
        assert groupIdToPost != 0;

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

        if(!joinedChannels.contains(event.getEventObject())) {
            joinedChannels.add(event.getEventObject());

            channelService.updateChannelIdAndHash(event.getEventObject());
        }
    }
}
