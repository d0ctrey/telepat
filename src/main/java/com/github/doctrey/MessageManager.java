package com.github.doctrey;

import org.telegram.api.engine.RpcCallbackEx;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.messages.TLRequestMessagesReadHistory;
import org.telegram.api.input.peer.TLAbsInputPeer;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.messages.TLAffectedMessages;
import org.telegram.api.peer.TLPeerChat;
import org.telegram.api.peer.TLPeerUser;

import java.util.logging.Logger;

/**
 * Created by Soheil on 12/24/17.
 */
public class MessageManager {

    private static final Logger LOGGER = Logger.getLogger(MessageManager.class.getSimpleName());

    private TelegramApi api;

    public MessageManager(TelegramApi api) {
        this.api = api;
    }

    public void markAsRead(TLAbsMessage absMessage) {
        TLAbsInputPeer absInputPeer = null;
        if (absMessage instanceof TLMessage) {
            TLMessage message = (TLMessage) absMessage;
            if(message.getToId() instanceof TLPeerUser)
                return;
//                absInputPeer = new TLInputPeerContact(message.getFromId());
            else if(message.getToId() instanceof TLPeerChat) {
               /* if(((TLPeerChat) message.getToId()).getChatId() != 240638145)
                    return;
                absInputPeer = new TLInputPeerChat(((TLPeerChat) message.getToId()).getChatId());*/
            }
        }/* else if (absMessage instanceof TLMessageForwarded) {
            *//*TLMessageForwarded messageForwarded = (TLMessageForwarded) absMessage;
            if(messageForwarded.getToId() instanceof TLPeerUser)
                return;
//                absInputPeer = new TLInputPeerContact(messageForwarded.getFromId());
            else if(messageForwarded.getToId() instanceof TLPeerChat) {
                if(((TLPeerChat) messageForwarded.getToId()).getChatId() != 240638145)
                    return;
                absInputPeer = new TLInputPeerChat(((TLPeerChat) messageForwarded.getToId()).getChatId());
            }*//*
        } */

        /*if (absInputPeer != null)
            readHistory(absMessage.getId(), absInputPeer, 0);*/

    }

    private void readHistory(int messageId, TLAbsInputPeer inputPeer, int offset) {
        api.doRpcCall(new TLRequestMessagesReadHistory(), new RpcCallbackEx<TLAffectedMessages>() {
            @Override
            public void onConfirmed() {

            }

            @Override
            public void onResult(TLAffectedMessages tlAffectedMessages) {

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
