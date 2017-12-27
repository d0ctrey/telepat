package com.github.doctrey.telegram.client.update.impl;


import com.github.doctrey.telegram.client.facade.UpdateService;
import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.updates.TLUpdatesTooLong;

/**
 * Created by s_tayari on 12/24/2017.
 */
public class UpdatesTooLongHandler implements AbsUpdatesHandler<TLUpdatesTooLong> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatesTooLongHandler.class);

    private UpdateService updateService;
    private TelegramApi api;


    public UpdatesTooLongHandler(TelegramApi api) {
        this.api = api;
        updateService = new UpdateService(api);
    }

    @Override
    public boolean canProcess(int updatesClassId) {
        return TLUpdatesTooLong.CLASS_ID == updatesClassId;
    }

    @Override
    public void processUpdates(TLUpdatesTooLong updates) {
        updateService.getRecentUpdates();
    }
}
