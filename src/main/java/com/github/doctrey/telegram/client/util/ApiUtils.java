package com.github.doctrey.telegram.client.util;

import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.DefaultApiCallback;
import com.github.doctrey.telegram.client.api.ApiConstants;
import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import com.github.doctrey.telegram.client.update.impl.*;
import org.telegram.api.TLConfig;
import org.telegram.api.TLNearestDc;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.help.TLRequestHelpGetNearestDc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Soheil on 4/14/18.
 */
public class ApiUtils {

    private static final String TAG = "ApiUtils";

    public static TelegramApi createNewClient(String phoneNumber) {
        DbApiStorage apiStateStorage = new DbApiStorage(phoneNumber);
        DefaultApiCallback apiCallback = new DefaultApiCallback();
        TelegramApi api = new TelegramApi(apiStateStorage, new AppInfo(ApiConstants.API_ID,
                System.getenv("TL_DEVICE_MODEL"), System.getenv("TL_DEVICE_VERSION"), "0.0.1", "en"), apiCallback);

        List<AbsUpdatesHandler> updatesHandlers = new ArrayList<>();
        ChannelNewMessageHandler channelNewMessageHandler = new ChannelNewMessageHandler(api);
        updatesHandlers.add(new UpdatesHandler(api, Arrays.asList(channelNewMessageHandler)));
        updatesHandlers.add(new UpdateShortHandler(api, Arrays.asList(new UserStatusHandler(api))));
        updatesHandlers.add(new UpdatesTooLongHandler(api));

        // setting handlers
        apiCallback.setUpdatesHandlers(updatesHandlers);

        RpcUtils rpcUtils = new RpcUtils(api);
        TLConfig config = null;
        try {
            config = rpcUtils.doRpc(new TLRequestHelpGetConfig(), false);
        } catch (Exception e) {
            Logger.w(TAG, "Failed to get the config.");
        }
        apiStateStorage.updateSettings(config);
        api.resetConnectionInfo();

        TLNearestDc tlNearestDc = null;
        try {
            tlNearestDc = rpcUtils.doRpc(new TLRequestHelpGetNearestDc(), false);
        } catch (Exception e) {
            Logger.w(TAG, "Failed to get the nearest DC.");
        }
        rpcUtils.switchToDc(tlNearestDc.getNearestDc());

        return api;
    }
}
