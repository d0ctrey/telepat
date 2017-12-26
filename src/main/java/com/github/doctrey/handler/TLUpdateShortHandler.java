package com.github.doctrey.handler;

import org.telegram.api.engine.TelegramApi;
import org.telegram.api.update.TLAbsUpdate;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.api.updates.TLUpdateShort;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by s_tayari on 12/24/2017.
 */
public class TLUpdateShortHandler implements TLAbsUpdatesHandler {

    private static final Logger LOGGER = Logger.getLogger(TLUpdateShortHandler.class.getSimpleName());

    private List<TLAbsUpdateHandler> updateHandlers = new ArrayList<>();
    private TelegramApi api;

    public TLUpdateShortHandler(TelegramApi api) {
        this.api = api;
        updateHandlers.add(new TLAbsUpdatesUserStatusHandler(api));
    }

    @Override
    public boolean canProcess(int updateClassId) {
        return TLUpdateShort.CLASS_ID == updateClassId;
    }

    @Override
    public void processUpdates(TLAbsUpdates updates) {
        TLAbsUpdate tlAbsUpdate = ((TLUpdateShort) updates).getUpdate();
        for(TLAbsUpdateHandler updateHandler : updateHandlers) {
            if (updateHandler.canProcess(tlAbsUpdate.getClassId())) {
                updateHandler.processUpdate(tlAbsUpdate);
            }
        }
    }
}
