package com.github.doctrey.telegram.client.facade;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.messages.TLRequestMessagesImportChatInvite;
import org.telegram.api.updates.TLAbsUpdates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by s_tayari on 4/16/2018.
 */
public class GroupService {

    private static final String TAG = "GroupService";

    private TelegramApi api;
    private ListenerQueue listenerQueue;

    public void joinAdminGroups() {
        List<String> inviteLinkList = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT group_link FROM tl_admin_groups")) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    inviteLinkList.add(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            Logger.e(TAG, e);
            return;
        }


        for(String inviteLink : inviteLinkList) {
            String hash = inviteLink.split("/")[(inviteLink.split("/").length) - 1];
            TLRequestMessagesImportChatInvite importChatInvite = new TLRequestMessagesImportChatInvite();
            importChatInvite.setHash(hash);

            api.doRpcCall(importChatInvite, new AbstractRpcCallback<TLAbsUpdates>() {
                @Override
                public void onResult(TLAbsUpdates result) {

                }
            });
        }
    }

    public void setApi(TelegramApi api) {
        this.api = api;
    }

    public void setListenerQueue(ListenerQueue listenerQueue) {
        this.listenerQueue = listenerQueue;
    }
}
