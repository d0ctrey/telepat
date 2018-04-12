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

    private String phoneNumber;
    private String securityCode;

    @Override
    public void run() {
        DbApiStorage apiStateStorage = new DbApiStorage(phoneNumber);
        TelegramApi api = new TelegramApi(apiStateStorage, new AppInfo(ApiConstants.API_ID,
                System.getenv("TL_DEVICE_MODEL"), System.getenv("TL_DEVICE_VERSION"), "0.0.1", "en"), new DefaultApiCallback());

        RpcUtils rpcUtils = new RpcUtils(api);
        TLConfig config = rpcUtils.doRpc(new TLRequestHelpGetConfig(), false);
        apiStateStorage.updateSettings(config);
        api.resetConnectionInfo();

        TLNearestDc tlNearestDc = rpcUtils.doRpc(new TLRequestHelpGetNearestDc(), false);
        rpcUtils.switchToDc(tlNearestDc.getNearestDc());

        RegistrationService registrationService = new RegistrationService(api);
        registrationService.verifyCode(phoneNumber, securityCode);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
}
