package com.github.doctrey.telegram.client.util;

import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TelegramApi;
import org.telegram.tl.TLMethod;
import org.telegram.tl.TLObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class RpcUtils {

    private TelegramApi api;

    public RpcUtils(TelegramApi api) {
        this.api = api;
    }

    public <T extends TLObject> T doRpc(TLMethod<T> tlMethod, boolean authorizationRequired) {
        try {
            if (!authorizationRequired) {
                return api.doRpcCallNonAuth(tlMethod);
            } else {
                return api.doRpcCall(tlMethod);
            }
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            if (e instanceof RpcException) {
                int errorCode = ((RpcException) e).getErrorCode();
                String errorTag = ((RpcException) e).getErrorTag();
                if (errorCode == 303) {
                    String dcToSwitch = errorTag.substring(errorTag.length() - 1);
                    switchToDc(Integer.valueOf(dcToSwitch));
                }
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e1) {
                Thread.currentThread().interrupt();
                throw new RuntimeException();
            }
            // call till success
            return doRpc(tlMethod, authorizationRequired);
        }
    }

    public void switchToDc(int dc) {
        try {
            api.switchToDc(dc);
        } catch (Exception e) {
            e.printStackTrace();
            switchToDc(dc);
        }
    }
}
