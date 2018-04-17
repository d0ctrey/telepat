package com.github.doctrey.telegram.client.update.impl;


import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.updates.TLUpdatesTooLong;

/**
 * Created by s_tayari on 12/24/2017.
 */
public class UpdatesTooLongHandler implements AbsUpdatesHandler<TLUpdatesTooLong> {

    private TelegramApi api;


    public UpdatesTooLongHandler(TelegramApi api) {
        this.api = api;
    }

    @Override
    public boolean canProcess(int updatesClassId) {
        return TLUpdatesTooLong.CLASS_ID == updatesClassId;
    }

    @Override
    public void processUpdates(TLUpdatesTooLong updates) {

    }
}
