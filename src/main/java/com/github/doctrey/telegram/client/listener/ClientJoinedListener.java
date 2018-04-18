package com.github.doctrey.telegram.client.listener;

import com.github.doctrey.telegram.client.facade.GroupService;
import com.github.doctrey.telegram.client.listener.event.ClientJoinedEvent;

/**
 * Created by s_tayari on 4/15/2018.
 */
public class ClientJoinedListener extends AbstractListener<ClientJoinedEvent> {

    private static final String TAG = "ClientJoinedListener";

    private GroupService groupService;

    public ClientJoinedListener(ListenerQueue listenerQueue) {
        super(listenerQueue);
        this.groupService = new GroupService(listenerQueue);
    }

    @Override
    public Class<ClientJoinedEvent> getEventClass() {
        return ClientJoinedEvent.class;
    }

    @Override
    public void inform(ClientJoinedEvent event) {
        groupService.setApi(event.getApi());
        groupService.joinAdminGroups();
    }

}
