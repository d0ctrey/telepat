package com.github.doctrey.telegram.client.update.impl;

import com.github.doctrey.telegram.client.facade.MessageService;
import com.github.doctrey.telegram.client.update.AbsUpdateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.updates.TLUpdates;

import java.util.Optional;


/**
 * Created by s_tayari on 12/24/2017.
 */
public class ChannelNewMessageHandler implements AbsUpdateHandler<TLUpdates, TLUpdateChannelNewMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelNewMessageHandler.class);

    private TelegramApi api;
    private MessageService messageService;

    public ChannelNewMessageHandler(TelegramApi api) {
        this.api = api;
        messageService = new MessageService(api);
    }

    @Override
    public boolean canProcess(int updateClassId) {
        return TLUpdateNewMessage.CLASS_ID == updateClassId || TLUpdateChannelNewMessage.CLASS_ID == updateClassId;
    }

    @Override
    public void processUpdate(TLUpdates updatesContext, TLUpdateChannelNewMessage updateChannelNewMessage) {
        TLAbsMessage absMessage = updateChannelNewMessage.getMessage();
        if (absMessage instanceof TLMessage) {
            TLMessage message = (TLMessage) absMessage;
            TLAbsPeer toId = message.getToId();
            if (!isSubscribed(toId.getId()))
                return;
            TLChannel channel = (TLChannel) findChannel(updatesContext, toId.getId());
            messageService.markChannelHistoryAsRead(updateChannelNewMessage.getMessage(), channel);
        }
    }

    private boolean isSubscribed(int channelId) {
        return channelId == 1343528547;
        /*Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            statement = connection.prepareStatement("SELECT CHANNEL_ID FROM telepat.SUBSCRIBED_CHANNELS WHERE CHANNEL_ID = ?");
            statement.setInt(1, channelId);
            rs = statement.executeQuery();
            // TODO: 2/15/18 later check expiry too
            return rs.next();
        } catch (SQLException e) {
            LOGGER.error("", e);
            return false;
        } finally {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException ignored) {
                }
            if (statement != null)
                try {
                    statement.close();
                } catch (SQLException ignored) {

                }
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException ignored) {

                }

        }*/
    }

    private TLAbsChat findChannel(TLUpdates updates, int channelId) {
        Optional<TLAbsChat> first = updates.getChats().stream().filter(x -> x.getId() == channelId).findFirst();
        return first.orElse(null);
    }
}
