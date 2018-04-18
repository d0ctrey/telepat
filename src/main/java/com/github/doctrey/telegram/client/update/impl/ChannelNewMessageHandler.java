package com.github.doctrey.telegram.client.update.impl;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.facade.ChannelService;
import com.github.doctrey.telegram.client.facade.MessageService;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.update.AbsUpdateHandler;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.updates.TLUpdates;

import java.util.List;
import java.util.Optional;


/**
 * Created by s_tayari on 12/24/2017.
 */
public class ChannelNewMessageHandler extends AbstractAbsUpdateHandler<TLUpdates, TLUpdateChannelNewMessage> {

    private MessageService messageService;
    private ChannelService channelService;

    public ChannelNewMessageHandler(ListenerQueue listenerQueue) {
        super(listenerQueue);
        messageService = new MessageService();
        channelService = new ChannelService();
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
//            if (!channelWhiteList.contains(toId.getId()))
                return;
//            TLChannel channel = (TLChannel) findChannel(updatesContext, toId.getId());
//            messageService.setApi(api);
//            messageService.markChannelHistoryAsRead(updateChannelNewMessage.getMessage(), channel);
        }
    }

    private TLAbsChat findChannel(TLUpdates updates, int channelId) {
        Optional<TLAbsChat> first = updates.getChats().stream().filter(x -> x.getId() == channelId).findFirst();
        return first.orElse(null);
    }

}
