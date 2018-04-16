package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.facade.GroupService;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.input.peer.TLInputPeerSelf;

/**
 * Created by s_tayari on 4/15/2018.
 */
public class MemberJoinedListener implements Listener<TLInputPeerSelf> {

    private static final String TAG = "MemberJoinedListener";

    private TelegramApi api;
    private GroupService groupService;

    public MemberJoinedListener() {
        this(null);
    }

    public MemberJoinedListener(TelegramApi api) {
        this.api = api;
        groupService = new GroupService(api);
    }

    @Override
    public void inform(TLInputPeerSelf tlObject) {
        groupService.joinAdminGroups();
    }

    public void setApi(TelegramApi api) {
        this.api = api;
        groupService.setApi(api);
    }
}
