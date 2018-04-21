package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.api.ApiConstants;
import com.github.doctrey.telegram.client.listener.*;
import com.github.doctrey.telegram.client.subscription.ChannelExpirationTimer;
import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import com.github.doctrey.telegram.client.update.UpdateQueue;
import com.github.doctrey.telegram.client.update.impl.*;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.functions.users.TLRequestUsersGetFullUser;
import org.telegram.api.input.user.TLInputUserSelf;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.user.TLUser;
import org.telegram.api.user.TLUserFull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

/**
 * Created by Soheil on 2/16/18.
 */
public class RunnableApi implements Callable<TelegramApi> {

    private static final String TAG = "RunnableApi";

    private ListenerQueue listenerQueue;
    private String phoneNumber;
    private TelegramApi api;

    public RunnableApi(ListenerQueue listenerQueue) {
        this.listenerQueue = listenerQueue;
    }

    @Override
    public TelegramApi call() {
        if(api == null) {
            DbApiStorage apiStateStorage = new DbApiStorage(phoneNumber);
            DefaultApiCallback apiCallback = new DefaultApiCallback();
            api = new TelegramApi(apiStateStorage, new AppInfo(ApiConstants.API_ID,
                    System.getenv("TL_DEVICE_MODEL"), System.getenv("TL_DEVICE_VERSION"), "0.0.1", "en"), apiCallback);

            // creating handlers
            List<AbsUpdatesHandler> updatesHandlers = new ArrayList<>();
            updatesHandlers.add(new UpdatesHandler(Arrays.asList(new ChannelNewMessageHandler(api, listenerQueue))));
            updatesHandlers.add(new UpdateShortHandler(Arrays.asList(new UserStatusHandler(api, listenerQueue))));
            updatesHandlers.add(new UpdatesTooLongHandler());

            UpdateQueue updateQueue = new UpdateQueue();
            updateQueue.setUpdatesHandlers(updatesHandlers);
            apiCallback.setUpdateQueue(updateQueue);

            DbApiUpdateState apiUpdateState = new DbApiUpdateState(phoneNumber.replaceAll("\\+", ""));
//        if (apiUpdateState.getObj().getDate() == 0) {
            try {
                TLUpdatesState tlState = api.doRpcCall(new TLRequestUpdatesGetState());
                apiUpdateState.updateState(tlState);
            } catch (IOException | TimeoutException e) {
                Logger.e(TAG, e);
            }
//        }
        }

        listenerQueue.getListeners().addAll(Arrays.asList(
                new MessageViewedListener(listenerQueue),
                new ChannelExpiredListener(listenerQueue),
                new NewChannelMessageListener(listenerQueue)
        ));

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


        ChannelExpirationTimer subscriptionTimer = new ChannelExpirationTimer(api, listenerQueue);
        subscriptionTimer.startCheckingExpiration();
        /*ClientJoinedListener joinedListenerService = new ClientJoinedListener(api);
        joinedListenerService.inform(new TLInputPeerSelf());*/

        /*executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> processUpdates(new TLUpdatesTooLong()), 5, 30, TimeUnit.SECONDS);*/
        return api;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setApi(TelegramApi api) {
        this.api = api;
    }
}
