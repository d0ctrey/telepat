package com.github.doctrey.telegram.client.difference;

import com.github.doctrey.telegram.client.facade.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.updates.difference.TLAbsDifference;

/**
 * Created by Soheil on 12/26/17.
 */
public class NewMessageProcessor implements DifferenceProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewMessageProcessor.class);

    private MessageService messageService;
    private TelegramApi api;

    public NewMessageProcessor(TelegramApi api) {
        this.api = api;
        this.messageService = new MessageService(api);
    }

    @Override
    public void processDifference(TLAbsDifference absDifference) {

    }
}
