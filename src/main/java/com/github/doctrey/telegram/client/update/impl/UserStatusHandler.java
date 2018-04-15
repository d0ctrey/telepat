package com.github.doctrey.telegram.client.update.impl;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.update.AbsUpdateHandler;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.users.TLRequestUsersGetFullUser;
import org.telegram.api.input.user.TLInputUser;
import org.telegram.api.update.TLUpdateUserStatus;
import org.telegram.api.updates.TLUpdateShort;
import org.telegram.api.user.TLUser;
import org.telegram.api.user.TLUserFull;
import org.telegram.api.user.status.TLAbsUserStatus;
import org.telegram.api.user.status.TLUserStatusOffline;
import org.telegram.api.user.status.TLUserStatusOnline;

/**
 * Created by s_tayari on 12/24/2017.
 */
public class UserStatusHandler implements AbsUpdateHandler<TLUpdateShort, TLUpdateUserStatus> {

    private TelegramApi api;

    public UserStatusHandler(TelegramApi api) {
        this.api = api;
    }

    @Override
    public boolean canProcess(int updatesClassId) {
        return TLUpdateUserStatus.CLASS_ID == updatesClassId;
    }

    @Override
    public void processUpdate(TLUpdateShort updateShort, TLUpdateUserStatus update) {
        TLAbsUserStatus userStatus = update.getStatus();
        int userId = 0;
        boolean makeCall = false;
        String statusString = null;
        if (userStatus instanceof TLUserStatusOnline) {
            userId = update.getUserId();
            statusString = "online";
            makeCall = true;
        } else if (userStatus instanceof TLUserStatusOffline) {
            userId = update.getUserId();
            statusString = "offline";
            makeCall = true;
        }

        if (!makeCall)
            return;

        String finalStatusString = statusString;
        TLRequestUsersGetFullUser getFullUser = new TLRequestUsersGetFullUser();
        TLInputUser inputUser = new TLInputUser();
        inputUser.setUserId(userId);
        getFullUser.setId(inputUser);
        api.doRpcCall(getFullUser, new AbstractRpcCallback<TLUserFull>() {
            @Override
            public void onResult(TLUserFull result) {
                if (result.getUser() instanceof TLUser)
                    System.out.println("=============================================");
                    System.out.println(((TLUser) result.getUser()).getFirstName() + " " + ((TLUser) result.getUser()).getLastName() + " is [" + finalStatusString + "]");
                    System.out.println("=============================================");
            }
        });
    }
}
