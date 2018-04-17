package com.github.doctrey.telegram.client.subscription;

/**
 * Created by Soheil on 4/16/18.
 */
public enum ChannelType {

    PUBLIC(0), PRIVATE(1);

    private int code;

    ChannelType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ChannelType withCode(int code) {
        for (ChannelType status :
                values()) {
            if (status.code == code)
                return status;
        }

        return PUBLIC;
    }
}
