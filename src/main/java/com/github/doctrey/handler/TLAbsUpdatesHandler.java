package com.github.doctrey.handler;


import org.telegram.api.updates.TLAbsUpdates;

/**
 * Created by s_tayari on 12/24/2017.
 */
public interface TLAbsUpdatesHandler {

    boolean canProcess(int updateClassId);
    void processUpdates(TLAbsUpdates updates);
}
