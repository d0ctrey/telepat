package com.github.doctrey.telegram.client.listener.event;

/**
 * Created by Soheil on 4/15/18.
 */
public enum EventType {

    VIEW_CHANNEL_POST(1), JOIN_CHANNEL(1);

    private int code;

    EventType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
