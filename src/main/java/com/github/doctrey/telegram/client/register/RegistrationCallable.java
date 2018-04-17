package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.ApiUpdateState;
import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.util.ApiUtils;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.updates.TLUpdatesState;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class RegistrationCallable implements Callable<TelegramApi> {

    private static final String TAG = "RegistrationCallable";

    private String phoneNumber;
    private TelegramApi api;

    @Override
    public TelegramApi call() throws Exception {
        if(api == null)
            api = ApiUtils.createNewClient(phoneNumber);

        AbsApiState apiStateStorage = api.getState();
        RegistrationService registrationService = new RegistrationService();
        registrationService.setApi(api);
        if (!apiStateStorage.isAuthenticated()) {
            try {
                registrationService.sendCode();
            } catch (RegistrationException e) {
                return api;
            }
        }

        ApiUpdateState apiUpdateState = new ApiUpdateState(phoneNumber.replaceAll("\\+", ""));
        try {
            TLUpdatesState tlState = api.doRpcCall(new TLRequestUpdatesGetState());
            apiUpdateState.updateState(tlState);
        } catch (IOException | TimeoutException e) {
            Logger.e(TAG, e);
        }

        return api;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setApi(TelegramApi api) {
        this.api = api;
        this.phoneNumber = ((DbApiStorage) api.getState()).getPhoneNumber();
    }
}
