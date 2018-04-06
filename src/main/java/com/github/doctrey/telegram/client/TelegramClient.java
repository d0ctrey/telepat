package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.update.AbsUpdatesHandler;
import com.github.doctrey.telegram.client.update.impl.UpdateShortHandler;
import com.github.doctrey.telegram.client.update.impl.UpdatesHandler;
import com.github.doctrey.telegram.client.update.impl.UpdatesTooLongHandler;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.TLConfig;
import org.telegram.api.TLNearestDc;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.auth.TLSentCode;
import org.telegram.api.engine.*;
import org.telegram.api.functions.auth.TLRequestAuthSendCode;
import org.telegram.api.functions.auth.TLRequestAuthSignIn;
import org.telegram.api.functions.auth.TLRequestAuthSignUp;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.help.TLRequestHelpGetNearestDc;
import org.telegram.api.functions.messages.TLRequestMessagesGetDialogs;
import org.telegram.api.functions.updates.TLRequestUpdatesGetState;
import org.telegram.api.functions.users.TLRequestUsersGetFullUser;
import org.telegram.api.messages.dialogs.TLAbsDialogs;
import org.telegram.api.updates.TLAbsUpdates;
import org.telegram.api.updates.TLUpdatesState;
import org.telegram.api.updates.TLUpdatesTooLong;
import org.telegram.tl.TLMethod;
import org.telegram.tl.TLObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.TimeoutException;

public class TelegramClient {

    // TODO: 2/16/18 read this from environment
    private static final int GROUP_ID = 1;

    private static ScheduledExecutorService executorService;

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            // find active users start a thread for each one

            ExecutorService executorService = Executors.newFixedThreadPool(100);
            executorService.submit(new RunnableApi());
        } catch (SQLException e) {
            return;
        }

        // start a schedule to check for new inactive users

    }

    private static int generateRandomId() {
        return ThreadLocalRandom.current().nextInt(8, 16 + 1);
    }
}
