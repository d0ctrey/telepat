package com.github.doctrey.telegram.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.RpcCallbackEx;
import org.telegram.tl.TLObject;

/**
 * Created by Soheil on 12/26/17.
 */
public abstract class AbstractRpcCallback<T extends TLObject> implements RpcCallbackEx<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRpcCallback.class);

    @Override
    public void onConfirmed() {

    }

    @Override
    public abstract void onResult(T result);

    @Override
    public void onError(int errorCode, String errorText) {
        LOGGER.error("RPC call failed with error -----> {} {}", errorCode, errorText);
    }
}
