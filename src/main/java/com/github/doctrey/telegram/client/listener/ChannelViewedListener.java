package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import com.github.doctrey.telegram.client.util.MessageUtils;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.messages.TLRequestMessagesSendMessage;
import org.telegram.api.input.peer.TLInputPeerChannel;
import org.telegram.api.input.peer.TLInputPeerChat;
import org.telegram.api.updates.TLAbsUpdates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Soheil on 12/28/17.
 */
public class ChannelViewedListener implements Listener<TLInputPeerChannel> {

    private static final String TAG = "ChannelViewedListener";

    private TelegramApi api;

    public ChannelViewedListener(TelegramApi api) {
        this.api = api;
    }

    @Override
    public void inform(TLInputPeerChannel tlObject) {
        int groupId = 0;
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT group_id FROM tl_admin_groups WHERE group_type = ?")) {
            statement.setInt(1, EventType.VIEW_CHANNEL_POST.getCode());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    groupId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            org.telegram.api.engine.Logger.e(TAG, e);
            return;
        }

        assert groupId != 0;

        TLInputPeerChat inputPeerChat = new TLInputPeerChat();
        inputPeerChat.setChatId(groupId);
        TLRequestMessagesSendMessage sendMessage = new TLRequestMessagesSendMessage();
        sendMessage.setRandomId(MessageUtils.generateRandomId());
        sendMessage.setMessage("Read history of channel " + tlObject.getChannelId() + ".");
        sendMessage.setPeer(inputPeerChat);

        api.doRpcCall(sendMessage, new AbstractRpcCallback<TLAbsUpdates>() {
            @Override
            public void onResult(TLAbsUpdates result) {

            }
        });

    }
}
