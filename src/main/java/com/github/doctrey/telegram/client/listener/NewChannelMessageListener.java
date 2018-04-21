package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.facade.MessageService;
import com.github.doctrey.telegram.client.listener.event.NewChannelMessageUpdate;
import org.telegram.api.chat.TLAbsChat;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.updates.TLUpdates;

import java.util.Optional;

/**
 * Created by s_tayari on 4/19/2018.
 */
public class NewChannelMessageListener extends AbstractChannelListener<NewChannelMessageUpdate> {

    private MessageService messageService;

    public NewChannelMessageListener(ListenerQueue listenerQueue) {
        super(listenerQueue);
        messageService = new MessageService(listenerQueue);
    }

    @Override
    public Class<NewChannelMessageUpdate> getEventClass() {
        return NewChannelMessageUpdate.class;
    }

    @Override
    public void inform(NewChannelMessageUpdate event) {
        if(!channelListInitialized)
            initializedChannelList(event.getApi());

        TLUpdateChannelNewMessage updateChannelNewMessage = event.getEventObject();
        TLUpdates updatesContext = event.getUpdateContext();
        TLAbsMessage absMessage = updateChannelNewMessage.getMessage();
        if (absMessage instanceof TLMessage) {
            TLMessage message = (TLMessage) absMessage;
            TLAbsPeer toId = message.getToId();
            if (channelWhiteList.keySet().stream().noneMatch(channel -> channel.getChannelId() == toId.getId()))
                return;
            TLChannel channel = (TLChannel) findChannel(updatesContext, toId.getId());
            messageService.setApi(event.getApi());
            messageService.markChannelHistoryAsRead(updateChannelNewMessage.getMessage(), channel);
        }
    }

    private TLAbsChat findChannel(TLUpdates updates, int channelId) {
        Optional<TLAbsChat> first = updates.getChats().stream().filter(x -> x.getId() == channelId).findFirst();
        return first.orElse(null);
    }
}
