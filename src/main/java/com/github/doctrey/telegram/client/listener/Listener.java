package com.github.doctrey.telegram.client.listener;

import org.telegram.tl.TLObject;

/**
 * Created by Soheil on 12/28/17.
 */
public interface Listener<T extends TLObject> {

    void inform(T object);
}
