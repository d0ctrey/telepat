package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.listener.event.MessageViewedEvent;
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

/**
 * Created by Soheil on 12/28/17.
 */
public class MessageViewedListener implements Listener<MessageViewedEvent> {

    private static final String TAG = "MessageViewedListener";

    private int groupId;

    public MessageViewedListener() {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT group_id FROM tl_admin_groups WHERE group_type = ?")) {
            statement.setInt(1, EventType.VIEW_CHANNEL_POST.getCode());
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
    public Class<MessageViewedEvent> getEventClass() {
        return MessageViewedEvent.class;
    }

    @Override
    public void inform(MessageViewedEvent event) {
        assert groupId != 0;

        TLInputPeerChat inputPeerChat = new TLInputPeerChat();
        inputPeerChat.setChatId(groupId);
        TLRequestMessagesSendMessage sendMessage = new TLRequestMessagesSendMessage();
        sendMessage.setRandomId(MessageUtils.generateRandomId());
        sendMessage.setMessage("Read history of channel " + event.getEventObject().getChannelId() + ".");
        sendMessage.setPeer(inputPeerChat);

        event.getApi().doRpcCall(sendMessage, new AbstractRpcCallback<TLAbsUpdates>() {
            @Override
            public void onResult(TLAbsUpdates result) {

            }
        });

    }
}
