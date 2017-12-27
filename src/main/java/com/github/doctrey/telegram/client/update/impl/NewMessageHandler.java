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
import org.telegram.api.user.TLUser;


/**
 * Created by s_tayari on 12/24/2017.
 */
public class NewMessageHandler implements AbsUpdateHandler<TLUpdates, TLUpdateChannelNewMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewMessageHandler.class);

    private TelegramApi api;
    private MessageService messageService;

    public NewMessageHandler(TelegramApi api) {
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
        if(absMessage instanceof TLMessage) {
            TLMessage message = (TLMessage) absMessage;
            TLAbsPeer toId = message.getToId();
            TLChannel channel = (TLChannel) findChannel(updatesContext, toId.getId());
            messageService.markChannelAsRead(updateChannelNewMessage.getMessage(), channel);
        }


    }

    public TLAbsChat findChannel(TLUpdates updates, int channelId) {
        return updates.getChats().stream().filter(x -> x.getId() == channelId).findFirst().get();
    }
}
