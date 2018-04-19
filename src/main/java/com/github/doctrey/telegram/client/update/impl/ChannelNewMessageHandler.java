package com.github.doctrey.telegram.client.update.impl;

import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.listener.event.NewChannelMessageUpdate;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.update.TLUpdateNewMessage;
import org.telegram.api.updates.TLUpdates;


/**
 * Created by s_tayari on 12/24/2017.
 */
public class ChannelNewMessageHandler extends AbstractAbsUpdateHandler<TLUpdates, TLUpdateChannelNewMessage> {


    public ChannelNewMessageHandler(TelegramApi api, ListenerQueue listenerQueue) {
        super(api, listenerQueue);
    }

    @Override
    public boolean canProcess(int updateClassId) {
        return TLUpdateNewMessage.CLASS_ID == updateClassId || TLUpdateChannelNewMessage.CLASS_ID == updateClassId;
    }

    @Override
    public void processUpdate(TLUpdates updatesContext, TLUpdateChannelNewMessage updateChannelNewMessage) {
        NewChannelMessageUpdate channelMessageUpdate = new NewChannelMessageUpdate(updateChannelNewMessage, api);
        channelMessageUpdate.setUpdateContext(updatesContext);
        listenerQueue.publish(channelMessageUpdate);
    }
}
