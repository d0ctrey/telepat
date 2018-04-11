package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import com.github.doctrey.telegram.client.update.impl.UpdateShortHandler;
import com.github.doctrey.telegram.client.update.impl.UpdatesHandler;
import com.github.doctrey.telegram.client.update.impl.UpdatesTooLongHandler;
import org.telegram.api.TLConfig;
import org.telegram.api.TLNearestDc;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.auth.TLSentCode;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.RpcException;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.auth.TLRequestAuthSignIn;
import org.telegram.api.functions.auth.TLRequestAuthSignUp;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.help.TLRequestHelpGetNearestDc;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.updates.TLUpdatesTooLong;
import org.telegram.tl.TLMethod;
import org.telegram.tl.TLObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Soheil on 2/16/18.
 */
public class RunnableApi implements Runnable {

    static TelegramApi api;
    static List<AbsUpdatesHandler> updatesHandlers = new ArrayList<>();

    private static final int API_ID = 33986;
    private static final String API_HASH = "cbf75f71f7b931f7d137a60d318590dd";
    private static final String PHONE_NUMBER = "+989123106718";
    private String phoneNumber;

    public class DefaultApiCallback implements ApiCallback {

        @Override
        public void onUpdatesInvalidated(TelegramApi _api) {
        }

        @Override
        public void onAuthCancelled(TelegramApi _api) {
        }

        @Override
        public void onUpdate(TLAbsUpdates updates) {
            processUpdates(updates);
        }
    }

    @Override
    public void run() {
        DbApiStorage apiStateStorage = null;
        try {
            apiStateStorage = new DbApiStorage(phoneNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }

        api = new TelegramApi(apiStateStorage, new AppInfo(API_ID,
                System.getenv("TL_DEVICE_MODEL"), System.getenv("TL_DEVICE_VERSION"), "0.0.1", "en"), new DefaultApiCallback());

        if (!apiStateStorage.isAuthenticated()) {
            TLConfig config = doRpc(new TLRequestHelpGetConfig(), false);
            apiStateStorage.updateSettings(config);
            api.resetConnectionInfo();

            TLNearestDc tlNearestDc = doRpc(new TLRequestHelpGetNearestDc(), false);
            switchToDc(tlNearestDc.getNearestDc());

            TLAuthorization authorization = handleRegistration();
            apiStateStorage.doAuth(authorization);
            apiStateStorage.setAuthenticated(api.getState().getPrimaryDc(), true);

        }

        ApiUpdateState apiUpdateState = new ApiUpdateState(PHONE_NUMBER.replaceAll("\\+", ""));
        if (apiUpdateState.getObj().getDate() == 0) {
            try {
                TLUpdatesState tlState = api.doRpcCall(new TLRequestUpdatesGetState());
                apiUpdateState.updateState(tlState);
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }

        /*if("https://telegram.me/joinchat/Cs1ppj6BBdQNe9LTcafLqg".startsWith("https://telegram.me/joinchat/"))
            hash = "https://telegram.me/joinchat/Cs1ppj6BBdQNe9LTcafLqg".substring("https://telegram.me/joinchat/".length() - 1);
        doRpc(new TLRequestUsersGetFullUser(new TLInputUserForeign());*/

        updatesHandlers.add(new UpdatesHandler(api));
        updatesHandlers.add(new UpdateShortHandler(api));
        updatesHandlers.add(new UpdatesTooLongHandler(api));
        // check for updates since restart
        processUpdates(new TLUpdatesTooLong());

        /*executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> processUpdates(new TLUpdatesTooLong()), 5, 30, TimeUnit.SECONDS);*/
    }

    private void processUpdates(TLAbsUpdates updates) {
        for (AbsUpdatesHandler updatesHandler : updatesHandlers) {
            if (updatesHandler.canProcess(updates.getClassId()))
                updatesHandler.processUpdates(updates);
        }
    }

    private TLAuthorization handleRegistration() {
        TLRequestAuthSendCode requestAuthSendCode = new TLRequestAuthSendCode();
        requestAuthSendCode.setApiId(API_ID);
        requestAuthSendCode.setApiHash(API_HASH);
        requestAuthSendCode.setPhoneNumber(PHONE_NUMBER);
        TLSentCode sentCode = doRpc(requestAuthSendCode, false);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.println("Enter Code: ");
        String code = null;
        try {
            code = reader.readLine();
        } catch (IOException e) {

        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TLAuthorization authorization;
        if (sentCode.isPhoneRegistered()) {
            TLRequestAuthSignIn requestAuthSignIn = new TLRequestAuthSignIn();
            requestAuthSignIn.setPhoneNumber(PHONE_NUMBER);
            requestAuthSignIn.setPhoneCodeHash(sentCode.getPhoneCodeHash());
            requestAuthSignIn.setPhoneCode(code);
            authorization = doRpc(requestAuthSignIn, false);
        } else {
            TLRequestAuthSignUp requestAuthSignUp = new TLRequestAuthSignUp();
            requestAuthSignUp.setFirstName("Soheil");
            requestAuthSignUp.setLastName("Tayari");
            requestAuthSignUp.setPhoneCode(code);
            requestAuthSignUp.setPhoneNumber(PHONE_NUMBER);
            requestAuthSignUp.setPhoneCodeHash(sentCode.getPhoneCodeHash());
            authorization = doRpc(requestAuthSignUp, false);
        }

        api.getState().setAuthenticated(api.getState().getPrimaryDc(), true);
        return authorization;
    }

    private <T extends TLObject> T doRpc(TLMethod<T> tlMethod, boolean authorizationRequired) {
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


    private void switchToDc(int dc) {
        try {
            api.switchToDc(dc);
        } catch (Exception e) {
            e.printStackTrace();
            switchToDc(dc);
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
