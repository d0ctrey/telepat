package com.github.doctrey.telegram.client.difference;

import org.telegram.api.updates.difference.TLAbsDifference;

/**
 * Created by Soheil on 12/26/17.
 */
public interface DifferenceProcessor {

    void processDifference(TLAbsDifference absDifference);
}
