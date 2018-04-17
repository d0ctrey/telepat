package com.github.doctrey.telegram.client.update.impl;

import com.github.doctrey.telegram.client.update.AbsUpdateHandler;
import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.updates.TLUpdateShort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s_tayari on 12/24/2017.
 */
public class UpdateShortHandler implements AbsUpdatesHandler<TLUpdateShort> {

    private List<AbsUpdateHandler> updateHandlers = new ArrayList<>();
    private TelegramApi api;

    public UpdateShortHandler(TelegramApi api, List<? extends AbsUpdateHandler> absUpdateHandlers) {
        this.api = api;
        updateHandlers.addAll(absUpdateHandlers);
    }

    @Override
    public boolean canProcess(int updatesClassId) {
        return TLUpdateShort.CLASS_ID == updatesClassId;
    }

    @Override
    public void processUpdates(TLUpdateShort updateShort) {
        for(AbsUpdateHandler updateHandler : updateHandlers) {
            if (updateHandler.canProcess(updateShort.getUpdate().getClassId())) {
                updateHandler.processUpdate(updateShort, updateShort.getUpdate());
            }
        }
    }
}
