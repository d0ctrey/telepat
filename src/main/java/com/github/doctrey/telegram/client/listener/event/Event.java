package com.github.doctrey.telegram.client.listener.event;

import org.telegram.tl.TLObject;

/**
 * Created by Soheil on 4/17/18.
 */
public interface Event<T> {

    T getTlObject();
}
