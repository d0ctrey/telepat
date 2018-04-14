package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.api.ApiConstants;
import com.github.doctrey.telegram.client.util.RpcUtils;
import org.telegram.api.TLConfig;
import org.telegram.api.TLNearestDc;
import org.telegram.api.auth.TLCheckedPhone;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.auth.TLRequestAuthCheckPhone;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.help.TLRequestHelpGetNearestDc;

/**
 * Created by Soheil on 4/13/18.
 */
public class Main {

    private static final String PHONE_NUMBER = "8615504466140";

    public static void main(String[] args) throws Exception {
        ApiStorage apiStateStorage = new ApiStorage(PHONE_NUMBER);
        DefaultApiCallback apiCallback = new DefaultApiCallback();
        TelegramApi api = new TelegramApi(apiStateStorage, new AppInfo(ApiConstants.API_ID,
                System.getenv("TL_DEVICE_MODEL"), System.getenv("TL_DEVICE_VERSION"), "0.0.1", "en"), apiCallback);

        RpcUtils rpcUtils = new RpcUtils(api);
        TLConfig config = rpcUtils.doRpc(new TLRequestHelpGetConfig(), false);
        apiStateStorage.updateSettings(config);
        api.resetConnectionInfo();

        TLNearestDc tlNearestDc = rpcUtils.doRpc(new TLRequestHelpGetNearestDc(), false);
        rpcUtils.switchToDc(tlNearestDc.getNearestDc());

        TLRequestAuthCheckPhone requestAuthCheckPhone = new TLRequestAuthCheckPhone();
        requestAuthCheckPhone.setPhoneNumber(PHONE_NUMBER);
        TLCheckedPhone checkedPhone = rpcUtils.doRpc(requestAuthCheckPhone, false);
    }
}
