package com.github.doctrey.telegram.client.inform;

import com.github.doctrey.telegram.client.AbstractRcpCallback;
import com.github.doctrey.telegram.client.util.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.messages.TLRequestMessagesSendMessage;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.input.peer.TLAbsInputPeer;
import org.telegram.api.input.peer.TLInputPeerChat;
import org.telegram.api.updates.TLAbsUpdates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Soheil on 12/28/17.
 */
public class InformChannelRead implements InformUserService<TLInputChannel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InformChannelRead.class);

    private TelegramApi api;

    public InformChannelRead(TelegramApi api) {
        this.api = api;
    }

    @Override
    public void inform(TLInputChannel object) {
        findPeers().forEach(absInputPeer -> {
            TLRequestMessagesSendMessage sendMessage = new TLRequestMessagesSendMessage();
            sendMessage.setRandomId(MessageUtils.generateRandomId());
            sendMessage.setMessage("Read history of channel " + object.getChannelId() + ".");
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
}
