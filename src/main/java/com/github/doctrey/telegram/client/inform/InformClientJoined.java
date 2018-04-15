package com.github.doctrey.telegram.client.inform;

import com.github.doctrey.telegram.client.AbstractRcpCallback;
import com.github.doctrey.telegram.client.util.MessageUtils;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.messages.TLRequestMessagesSendMessage;
import org.telegram.api.input.peer.TLAbsInputPeer;
import org.telegram.api.input.peer.TLInputPeerChat;
import org.telegram.api.input.peer.TLInputPeerSelf;
import org.telegram.api.updates.TLAbsUpdates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s_tayari on 4/15/2018.
 */
public class InformClientJoined implements InformUserService<TLInputPeerSelf> {

    private TelegramApi api;

    @Override
    public void inform(TLInputPeerSelf object) {
        findPeers().forEach(absInputPeer -> {
            TLRequestMessagesSendMessage sendMessage = new TLRequestMessagesSendMessage();
            sendMessage.setRandomId(MessageUtils.generateRandomId());
            sendMessage.setMessage("Hey, I've joined the team.");
            sendMessage.setPeer(absInputPeer);

            api.doRpcCall(sendMessage, new AbstractRcpCallback<TLAbsUpdates>() {
                @Override
                public void onResult(TLAbsUpdates result) {

                }
            });
        });
    }

    private List<TLAbsInputPeer> findPeers() {
        List<TLAbsInputPeer> inputPeers = new ArrayList<>();
        TLInputPeerChat inputPeerChat = new TLInputPeerChat();
        inputPeerChat.setChatId(240638145);
        inputPeers.add(inputPeerChat);

        return inputPeers;

    }

    public void setApi(TelegramApi api) {
        this.api = api;
    }
}
