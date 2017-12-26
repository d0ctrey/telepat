package com.github.doctrey.handler;

import org.telegram.api.update.TLAbsUpdate;

/**
 * Created by s_tayari on 12/24/2017.
 */
public interface TLAbsUpdateHandler {

    boolean canProcess(int updateClassId);
    void processUpdate(TLAbsUpdate update);
}
