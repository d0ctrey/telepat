package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.api.ApiConstants;
import com.github.doctrey.telegram.client.util.MessageUtils;
import com.github.doctrey.telegram.client.util.RpcUtils;
import org.telegram.api.TLConfig;
import org.telegram.api.TLNearestDc;
import org.telegram.api.auth.TLCheckedPhone;
import org.telegram.api.engine.AppInfo;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.auth.TLRequestAuthCheckPhone;
import org.telegram.api.functions.help.TLRequestHelpGetConfig;
import org.telegram.api.functions.help.TLRequestHelpGetNearestDc;

/**
 * Created by Soheil on 4/13/18.
 */
public class Main {

    private static final String PHONE_NUMBER = "8615504466140";

    public static void main(String[] args) throws Exception {
        System.out.println(MessageUtils.generateRandomId());
    }
}
