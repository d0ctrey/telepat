package com.github.doctrey.telegram.client.update.impl;

import com.github.doctrey.telegram.client.AbstractRcpCallback;
import com.github.doctrey.telegram.client.TelegramClient;
import com.github.doctrey.telegram.client.update.AbsUpdateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.RpcCallbackEx;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.users.TLRequestUsersGetFullUser;
import org.telegram.api.update.TLAbsUpdate;
import org.telegram.api.update.TLUpdateUserStatus;
import org.telegram.api.user.TLUser;
import org.telegram.api.user.TLUserFull;
import org.telegram.api.user.status.TLAbsUserStatus;
import org.telegram.api.user.status.TLUserStatusOffline;
import org.telegram.api.user.status.TLUserStatusOnline;

/**
 * Created by s_tayari on 12/24/2017.
 */
public class UserStatusHandler implements AbsUpdateHandler<TLUpdateUserStatus> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserStatusHandler.class);

    private TelegramApi api;

    public UserStatusHandler(TelegramApi api) {
        this.api = api;
    }

    @Override
    public boolean canProcess(int updateClassId) {
        return TLUpdateUserStatus.CLASS_ID == updateClassId;
    }

    @Override
    public void processUpdate(TLUpdateUserStatus update) {
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
        api.doRpcCall(getFullUser, new AbstractRcpCallback<TLUserFull>() {
            @Override
            public void onResult(TLUserFull result) {
                if(result.getUser() instanceof TLUser)
                    System.out.println("### " + ((TLUser) result.getUser()).getFirstName() + " " + ((TLUser) result.getUser()).getLastName() + " is " + finalStatusString + " ###");
            }
        });
    }
}
