package com.github.doctrey.telegram.client.update.impl;

import com.github.doctrey.telegram.client.update.AbsUpdateHandler;
import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.update.TLAbsUpdate;
import org.telegram.api.updates.TLUpdates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s_tayari on 12/24/2017.
 */
public class UpdatesHandler implements AbsUpdatesHandler<TLUpdates> {

    private List<AbsUpdateHandler> updateHandlers = new ArrayList<>();

    public UpdatesHandler(List<? extends AbsUpdateHandler> absUpdateHandlers) {
        updateHandlers.addAll(absUpdateHandlers);
    }

    @Override
    public boolean canProcess(int updatesClassId) {
        return TLUpdates.CLASS_ID == updatesClassId;
    }

    @Override
    public void processUpdates(TLUpdates updates) {
        for(TLAbsUpdate update : updates.getUpdates()) {
            for (AbsUpdateHandler updateHandler : updateHandlers) {
                if(updateHandler.canProcess(update.getClassId()))
                    updateHandler.processUpdate(updates, update);
            }
        }
    }
}
