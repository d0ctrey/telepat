package com.github.doctrey.telegram.client.facade;

import com.github.doctrey.telegram.client.AbstractRcpCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.engine.RpcCallbackEx;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.channels.TLRequestChannelsGetFullChannel;
import org.telegram.api.functions.messages.TLRequestMessagesReadHistory;
import org.telegram.api.input.chat.TLAbsInputChannel;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.input.peer.TLAbsInputPeer;
import org.telegram.api.input.peer.TLInputPeerChannel;
import org.telegram.api.input.peer.TLInputPeerChat;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.TLAffectedMessages;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerChannel;
import org.telegram.api.peer.TLPeerChat;
import org.telegram.api.peer.TLPeerUser;
import org.telegram.api.user.TLAbsUser;

import java.util.Arrays;

/**
 * Created by Soheil on 12/24/17.
 */
public class MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private TelegramApi api;
    private final int[] whiteList = {1343528547};

    public MessageService(TelegramApi api) {
        this.api = api;
    }

    public void markAsRead(TLAbsMessage absMessage) {
        System.out.println(absMessage);
        /*TLAbsInputPeer absInputPeer = null;
        if (absMessage instanceof TLMessage) {
            TLMessage message = (TLMessage) absMessage;
            TLAbsPeer toPeer = message.getToId();
            if(toPeer instanceof TLPeerUser) {
                TLPeerUser peerUser = (TLPeerUser) toPeer;
                peerUser.
            } else if(message.getToId() instanceof TLPeerChat) {
                if(((TLPeerChat) message.getToId()).getChatId() != 240638145)
                    return;
                absInputPeer = new TLInputPeerChat(((TLPeerChat) message.getToId()).getChatId());
            } else if(toPeer instanceof TLPeerChannel) {
                int channelId = toPeer.getId();
                if(Arrays.asList(whiteList).contains(channelId)) {
                    TLRequestChannelsGetFullChannel getFullChannel = new TLRequestChannelsGetFullChannel();

                    getFullChannel.setChannel();
                    TLInputPeerChannel inputPeerChannel = new TLInputPeerChannel();
                    inputPeerChannel.set
                    readHistory(message.getId(), );
                }
            }
        }*/

        /*if (absInputPeer != null)
            readHistory(absMessage.getId(), absInputPeer, 0);*/

    }

    private void readHistory(int messageId, TLAbsInputPeer inputPeer) {
        TLRequestMessagesReadHistory readHistory = new TLRequestMessagesReadHistory();
        readHistory.setPeer(inputPeer);
        readHistory.setMaxId(messageId);
        api.doRpcCall(new TLRequestMessagesReadHistory(), new AbstractRcpCallback<TLAffectedMessages>() {
            @Override
            public void onResult(TLAffectedMessages result) {
                LOGGER.info("Read {} messages.", result.getPtsCount());
            }
        });
    }
}
