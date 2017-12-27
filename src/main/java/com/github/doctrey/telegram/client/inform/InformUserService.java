package com.github.doctrey.telegram.client.inform;

import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.tl.TLObject;

/**
 * Created by Soheil on 12/28/17.
 */
public interface InformUserService<T extends TLObject> {

    void inform(T object);
}
