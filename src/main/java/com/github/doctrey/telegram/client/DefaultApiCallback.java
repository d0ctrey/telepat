package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.updates.TLAbsUpdates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class DefaultApiCallback implements ApiCallback {

    private List<AbsUpdatesHandler> updatesHandlers = new ArrayList<>();

    @Override
    public void onUpdatesInvalidated(TelegramApi _api) {
    }

    @Override
    public void onAuthCancelled(TelegramApi _api) {
    }

    @Override
    public void onUpdate(TLAbsUpdates updates) {
        for (AbsUpdatesHandler updatesHandler : updatesHandlers) {
            if (updatesHandler.canProcess(updates.getClassId()))
                updatesHandler.processUpdates(updates);
        }
    }

    public void setUpdatesHandlers(List<AbsUpdatesHandler> updatesHandlers) {
        this.updatesHandlers = updatesHandlers;
    }
}
