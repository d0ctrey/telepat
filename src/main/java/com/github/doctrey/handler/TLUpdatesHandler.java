package com.github.doctrey.handler;

import org.telegram.api.engine.TelegramApi;
import org.telegram.api.update.TLAbsUpdate;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.api.updates.TLUpdates;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by s_tayari on 12/24/2017.
 */
public class TLUpdatesHandler implements TLAbsUpdatesHandler {

    private static final Logger LOGGER = Logger.getLogger(TLUpdatesHandler.class.getSimpleName());

    private List<TLAbsUpdateHandler> updateHandlers = new ArrayList<>();
    private TelegramApi api;

    public TLUpdatesHandler(TelegramApi api) {
        this.api = api;
        updateHandlers.add(new TLUpdateNewMessageHandler(api));
    }

    @Override
    public boolean canProcess(int updateClassId) {
        return TLUpdates.CLASS_ID == updateClassId;
    }

    @Override
    public void processUpdates(TLAbsUpdates updates) {
        TLUpdates tlUpdates = (TLUpdates) updates;
        for(TLAbsUpdate update : tlUpdates.getUpdates()) {
            for (TLAbsUpdateHandler updateHandler : updateHandlers) {
                if(updateHandler.canProcess(update.getClassId()))
                    updateHandler.processUpdate(update);
            }
        }
    }
}
