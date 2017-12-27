package com.github.doctrey.telegram.client.facade;

import com.github.doctrey.telegram.client.AbstractRcpCallback;
import com.github.doctrey.telegram.client.inform.InformChannelRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.engine.RpcCallbackEx;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.channels.TLRequestChannelsGetFullChannel;
import org.telegram.api.functions.channels.TLRequestChannelsReadHistory;
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
import org.telegram.api.user.TLUser;
import org.telegram.tl.TLBool;

import java.util.Arrays;

/**
 * Created by Soheil on 12/24/17.
 */
public class MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private TelegramApi api;
    private final Integer[] whiteList = {1343528547, 240638145};

    private InformChannelRead informChannelRead;

    public MessageService(TelegramApi api) {
        this.api = api;
        informChannelRead = new InformChannelRead(api);
    }

    public void markChannelHistoryAsRead(TLAbsMessage absMessage, TLChannel channel) {
        if (absMessage instanceof TLMessage) {
            TLMessage message = (TLMessage) absMessage;
            TLAbsPeer toPeer = message.getToId();
            if(toPeer instanceof TLPeerChannel) {
                int channelId = toPeer.getId();
                if(Arrays.asList(whiteList).contains(channelId)) {
                    TLInputChannel inputChannel = new TLInputChannel();
                    inputChannel.setChannelId(channel.getId());
                    inputChannel.setAccessHash(channel.getAccessHash());

                    TLRequestChannelsReadHistory readHistory = new TLRequestChannelsReadHistory();
                    readHistory.setChannel(inputChannel);
                    api.doRpcCall(readHistory, new AbstractRcpCallback<TLBool>() {
                        @Override
                        public void onResult(TLBool result) {
                            informChannelRead.inform(inputChannel);
                        }
                    });
                }
            }
        }
    }
}
