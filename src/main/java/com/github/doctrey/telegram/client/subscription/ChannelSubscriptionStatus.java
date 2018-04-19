package com.github.doctrey.telegram.client.subscription;

/**
 * Created by s_tayari on 4/16/2018.
 */
public enum ChannelSubscriptionStatus {

    PENDING(1), VERIFIED(2), VERIFIED_COMPLETE(3), EXPIRED(4), INACTIVE(5);

    private int code;

    ChannelSubscriptionStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ChannelSubscriptionStatus withCode(int code) {
        for (ChannelSubscriptionStatus status :
                values()) {
            if (status.code == code)
                return status;
        }

        return INACTIVE;
    }
}
