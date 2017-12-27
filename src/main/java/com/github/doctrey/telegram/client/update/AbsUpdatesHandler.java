package com.github.doctrey.telegram.client.update;


import org.telegram.api.updates.TLAbsUpdates;

/**
 * Created by s_tayari on 12/24/2017.
 */
public interface AbsUpdatesHandler<T extends TLAbsUpdates> {

    boolean canProcess(int updatesClassId);
    void processUpdates(T updates);
}
