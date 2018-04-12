package com.github.doctrey.telegram.client.register;

/**
 * Created by s_tayari on 4/11/2018.
 */
public enum PhoneNumberStatus {

    NEW(1), CODE_SENT(2), CODE_RECEIVED(3), REGISTERED(4), INACTIVE(5);

    private int code;

    PhoneNumberStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
