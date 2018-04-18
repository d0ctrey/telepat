package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.update.UpdateQueue;
import org.telegram.api.engine.ApiCallback;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.updates.TLAbsUpdates;

/**
 * Created by s_tayari on 4/12/2018.
 */
public class DefaultApiCallback implements ApiCallback {

    private static final String TAG = "DefaultApiCallback";

    private UpdateQueue updateQueue;

    @Override
    public void onUpdatesInvalidated(TelegramApi _api) {
        Logger.d(TAG, "");
    }

    @Override
    public void onAuthCancelled(TelegramApi _api) {
        Logger.d(TAG, "");
    }

    @Override
    public void onUpdate(TLAbsUpdates updates) {
        updateQueue.dispatch(updates);
    }

    public void setUpdateQueue(UpdateQueue updateQueue) {
        this.updateQueue = updateQueue;
    }
}
