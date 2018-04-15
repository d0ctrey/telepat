package com.github.doctrey.telegram.client.facade;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.ApiUpdateState;
import com.github.doctrey.telegram.client.difference.DifferenceProcessor;
import com.github.doctrey.telegram.client.difference.NewMessageProcessor;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.updates.TLRequestUpdatesGetDifference;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.updates.difference.TLAbsDifference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Soheil on 12/26/17.
 */
public class UpdateService {

    private TelegramApi api;
    private DbApiStorage apiStateStorage;
    private List<DifferenceProcessor> differenceProcessors = new ArrayList<>();

    public UpdateService(TelegramApi api) {
        this.api = api;
        this.apiStateStorage = (DbApiStorage) api.getState();
        this.differenceProcessors.add(new NewMessageProcessor(api));
    }

    public void getRecentUpdates() {
        ApiUpdateState apiUpdateState = new ApiUpdateState(apiStateStorage.getObj().getPhone().replaceAll("\\+", ""));
        TLUpdatesState lastState = apiUpdateState.getObj();

        TLRequestUpdatesGetDifference getDifference = new TLRequestUpdatesGetDifference();
        getDifference.setDate(lastState.getDate());
        getDifference.setPts(lastState.getPts());
        getDifference.setQts(lastState.getQts());
        api.doRpcCall(getDifference, new AbstractRpcCallback<TLAbsDifference>() {

            @Override
            public void onResult(TLAbsDifference result) {
                processDifference(result);
            }
        });
    }

    public void processDifference(TLAbsDifference result) {
        /*TLUpdatesState newState = null;
        if (result instanceof TLDifferenceSlice) {
            TLVector<TLAbsMessage> newMessages = result.getNewMessages();
            processNewMessages(newMessages);
            newState = ((TLDifferenceSlice) result).getIntermediateState();
        } else if (result instanceof TLDifference) {
            TLVector<TLAbsMessage> newMessages = result.getNewMessages();
            processNewMessages(newMessages);
            newState = ((TLDifference) result).getState();
        }

        if (newState != null)
            apiState.updateState(newState);*/
    }
}
