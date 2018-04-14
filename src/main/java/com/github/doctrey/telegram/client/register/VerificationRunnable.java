package com.github.doctrey.telegram.client.register;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.DefaultApiCallback;
import com.github.doctrey.telegram.client.api.ApiConstants;
import com.github.doctrey.telegram.client.util.RpcUtils;
import org.telegram.api.TLConfig;
import org.telegram.api.TLNearestDc;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.help.TLRequestHelpGetNearestDc;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class VerificationRunnable implements Runnable {

    private TelegramApi api;

    @Override
    public void run() {
        RegistrationService registrationService = new RegistrationService(api);
        try {
            registrationService.verifyCode();
        } catch (RegistrationException ignored) {

        }
    }

    public void setApi(TelegramApi api) {
        this.api = api;
    }
}
