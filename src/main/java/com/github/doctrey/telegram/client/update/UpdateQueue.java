package com.github.doctrey.telegram.client.update;

import org.telegram.api.updates.TLAbsUpdates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s_tayari on 4/18/2018.
 */
public class UpdateQueue {

    private List<AbsUpdatesHandler> updatesHandlers;

    public UpdateQueue() {
        updatesHandlers = new ArrayList<>();
    }

    public void dispatch(TLAbsUpdates updates) {
        for (AbsUpdatesHandler updatesHandler : updatesHandlers) {
            if (updatesHandler.canProcess(updates.getClassId()))
                updatesHandler.processUpdates(updates);
        }
    }

    public List<AbsUpdatesHandler> getUpdatesHandlers() {
        return updatesHandlers;
    }

    public void setUpdatesHandlers(List<AbsUpdatesHandler> updatesHandlers) {
        this.updatesHandlers = updatesHandlers;
    }
}
