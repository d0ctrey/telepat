package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.api.ApiConstants;
import com.github.doctrey.telegram.client.listener.MemberJoinedListener;
import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import com.github.doctrey.telegram.client.update.impl.UpdateShortHandler;
import com.github.doctrey.telegram.client.update.impl.UpdatesHandler;
import com.github.doctrey.telegram.client.update.impl.UpdatesTooLongHandler;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.functions.users.TLRequestUsersGetFullUser;
import org.telegram.api.input.peer.TLInputPeerSelf;
import org.telegram.api.input.user.TLInputUserSelf;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.user.TLUser;
import org.telegram.api.user.TLUserFull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Soheil on 2/16/18.
 */
public class RunnableApi implements Runnable {

    private static final String TAG = "RunnableApi";

    private String phoneNumber;

    @Override
    public void run() {
        DbApiStorage apiStateStorage = new DbApiStorage(phoneNumber);
        DefaultApiCallback apiCallback = new DefaultApiCallback();
        TelegramApi api = new TelegramApi(apiStateStorage, new AppInfo(ApiConstants.API_ID,
                System.getenv("TL_DEVICE_MODEL"), System.getenv("TL_DEVICE_VERSION"), "0.0.1", "en"), apiCallback);

        List<AbsUpdatesHandler> updatesHandlers = new ArrayList<>();
        updatesHandlers.add(new UpdatesHandler(api));
        updatesHandlers.add(new UpdateShortHandler(api));
        updatesHandlers.add(new UpdatesTooLongHandler(api));

        // setting handlers
        apiCallback.setUpdatesHandlers(updatesHandlers);

        DbApiUpdateState apiUpdateState = new DbApiUpdateState(phoneNumber.replaceAll("\\+", ""));
//        if (apiUpdateState.getObj().getDate() == 0) {
            try {
                TLUpdatesState tlState = api.doRpcCall(new TLRequestUpdatesGetState());
                apiUpdateState.updateState(tlState);
            } catch (IOException | TimeoutException e) {
                Logger.e(TAG, e);
            }
//        }

        TLRequestUsersGetFullUser getFullUser = new TLRequestUsersGetFullUser();
        getFullUser.setId(new TLInputUserSelf());
        api.doRpcCall(getFullUser, new AbstractRpcCallback<TLUserFull>() {
            @Override
            public void onResult(TLUserFull result) {
                System.out.println("===================================================");
                System.out.println("API for " + ((TLUser) result.getUser()).getFirstName() + " " + ((TLUser) result.getUser()).getLastName() + " started.");
                System.out.println("===================================================");
            }
        });

        MemberJoinedListener joinedListenerService = new MemberJoinedListener(api);
        joinedListenerService.inform(new TLInputPeerSelf());

        /*executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> processUpdates(new TLUpdatesTooLong()), 5, 30, TimeUnit.SECONDS);*/
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
