package com.github.doctrey.telegram.client.facade;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.DbApiStorage;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.account.TLAccountPasswordInputSettings;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.account.TLRequestAccountUpdatePasswordSettings;
import org.telegram.tl.TLBool;
import org.telegram.tl.TLBoolTrue;
import org.telegram.tl.TLBytes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Soheil on 4/16/18.
 */
public class SecurityService {

    private static final String TAG = "SecurityService";

    private ListenerQueue listenerQueue;
    private TelegramApi api;

    public SecurityService(ListenerQueue listenerQueue, TelegramApi api) {
        this.listenerQueue = listenerQueue;
        this.api = api;
    }

    public SecurityService(ListenerQueue listenerQueue) {
        this(listenerQueue, null);
    }

    public SecurityService() {
        this(null);
    }

    // TODO: 4/16/18 make this work later. Refer to telegram android app.
    public void setTelegramPassword(String password) {
        TLAccountPasswordInputSettings inputSettings = new TLAccountPasswordInputSettings();
        TLBytes tlBytes = new TLBytes(password.getBytes());
        inputSettings.setEmail("andrew.r.young65@gmail.com");
        inputSettings.setNewPasswordHash(tlBytes);
        inputSettings.setNewSalt(new TLBytes("".getBytes()));
        inputSettings.setHint("");
        inputSettings.setFlags(2);

        final String pass = password;
        TLRequestAccountUpdatePasswordSettings updatePasswordSettings = new TLRequestAccountUpdatePasswordSettings();
        updatePasswordSettings.setNewSettings(inputSettings);
        updatePasswordSettings.setCurrentPasswordHash(new TLBytes("".getBytes()));
        api.doRpcCall(updatePasswordSettings, new AbstractRpcCallback<TLBool>() {
            @Override
            public void onResult(TLBool result) {
                boolean success = result instanceof TLBoolTrue;
                if(!success)
                    return;
                Logger.d(TAG, "Password change result successful.");

                String phoneNumber = ((DbApiStorage) api.getState()).getPhoneNumber();
                try(Connection connection = ConnectionPool.getInstance().getConnection();
                    PreparedStatement statement = connection.prepareStatement("UPDATE tl_phone_numbers SET cloud_password = ? WHERE phone_number = ?")
                ) {

                    statement.setString(1, pass);
                    statement.setString(2, phoneNumber);

                    statement.executeUpdate();

                } catch (SQLException e) {
                    Logger.e(TAG, e);
                }

            }
        });
    }

    public void setApi(TelegramApi api) {
        this.api = api;
    }
}
