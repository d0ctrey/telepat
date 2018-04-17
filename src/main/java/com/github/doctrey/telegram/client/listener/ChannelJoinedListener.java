package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.listener.event.ChannelJoinedEvent;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import com.github.doctrey.telegram.client.util.MessageUtils;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.messages.TLRequestMessagesSendMessage;
import org.telegram.api.input.peer.TLInputPeerChat;
import org.telegram.api.updates.TLAbsUpdates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Soheil on 4/16/18.
 */
public class ChannelJoinedListener implements Listener<ChannelJoinedEvent> {

    private static final String TAG = "ChannelJoinedListener";

    private TelegramApi api;
    private int groupId;

    public ChannelJoinedListener(TelegramApi api) {
        this.api = api;

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT group_id FROM tl_admin_groups WHERE group_type = ?")) {
            statement.setInt(1, EventType.JOIN_CHANNEL.getCode());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    groupId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    @Override
    public void inform(ChannelJoinedEvent event) {
        assert groupId != 0;

        TLInputPeerChat inputPeerChat = new TLInputPeerChat();
        inputPeerChat.setChatId(groupId);
        TLRequestMessagesSendMessage sendMessage = new TLRequestMessagesSendMessage();
        sendMessage.setRandomId(MessageUtils.generateRandomId());
        sendMessage.setMessage("Joined channel " + event.getTlObject().getChannelId() + ".");
        sendMessage.setPeer(inputPeerChat);

        api.doRpcCall(sendMessage, new AbstractRpcCallback<TLAbsUpdates>() {
            @Override
            public void onResult(TLAbsUpdates result) {

            }
        });
    }
}
