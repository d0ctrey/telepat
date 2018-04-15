package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.DbApiUpdateState;
import com.github.doctrey.telegram.client.util.ApiUtils;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.updates.TLUpdatesState;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class VerificationRunnable implements Callable<TelegramApi> {

    private static final String TAG = "VerificationRunnable";

    private TelegramApi api;
    private String phoneNumber;

    @Override
    public TelegramApi call() {
        if(api == null)
            api = ApiUtils.createNewClient(phoneNumber);

        DbApiUpdateState apiUpdateState = new DbApiUpdateState(phoneNumber.replaceAll("\\+", ""));
        try {
            TLUpdatesState tlState = api.doRpcCall(new TLRequestUpdatesGetState());
            apiUpdateState.updateState(tlState);
        } catch (IOException | TimeoutException e) {
            Logger.e(TAG, e);
        }

        RegistrationService registrationService = new RegistrationService(api);
        try {
            registrationService.verifyCode();
        } catch (RegistrationException e) {
            Logger.e(TAG, e);
        }

        return api;
    }

    public void setApi(TelegramApi api) {
        this.api = api;
        this.phoneNumber = ((DbApiStorage) api.getState()).getPhoneNumber();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
