package com.github.doctrey.telegram.client.listener.event;

import org.telegram.api.engine.TelegramApi;
import org.telegram.api.update.TLUpdateChannelNewMessage;
import org.telegram.api.updates.TLUpdates;


/**
 * Created by s_tayari on 4/19/2018.
 */
public class NewChannelMessageUpdate extends AbstractEvent<TLUpdateChannelNewMessage> {

    private TLUpdates updateContext;

    public NewChannelMessageUpdate(TLUpdateChannelNewMessage eventObject, TelegramApi api) {
        super(eventObject, api);
    }

    public TLUpdates getUpdateContext() {
        return updateContext;
    }

    public void setUpdateContext(TLUpdates updateContext) {
        this.updateContext = updateContext;
    }
}
