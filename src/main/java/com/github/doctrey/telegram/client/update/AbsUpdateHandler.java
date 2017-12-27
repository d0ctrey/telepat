package com.github.doctrey.telegram.client.update;

import org.telegram.api.update.TLAbsUpdate;
import org.telegram.api.updates.TLAbsUpdates;

/**
 * Created by s_tayari on 12/24/2017.
 */
public interface AbsUpdateHandler<S extends TLAbsUpdates, T extends TLAbsUpdate> {

    boolean canProcess(int updateClassId);
    void processUpdate(S updatesContext, T thisUpdate);
}
