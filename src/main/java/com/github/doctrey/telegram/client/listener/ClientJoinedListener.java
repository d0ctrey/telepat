package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.facade.GroupService;
import com.github.doctrey.telegram.client.listener.event.ClientJoinedEvent;
import org.telegram.api.engine.TelegramApi;

/**
 * Created by s_tayari on 4/15/2018.
 */
public class ClientJoinedListener implements Listener<ClientJoinedEvent> {

    private static final String TAG = "ClientJoinedListener";

    private TelegramApi api;
    private GroupService groupService;

    public ClientJoinedListener(TelegramApi api) {
        this.api = api;
        groupService = new GroupService();
        groupService.setApi(api);
    }

    @Override
    public Class<ClientJoinedEvent> getEventClass() {
        return ClientJoinedEvent.class;
    }

    @Override
    public void inform(ClientJoinedEvent event) {
        groupService.joinAdminGroups();
    }

    public void setApi(TelegramApi api) {
        this.api = api;
        groupService.setApi(api);
    }
}
