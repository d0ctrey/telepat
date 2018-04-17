package com.github.doctrey.telegram.client.facade;

import com.github.doctrey.telegram.client.AbstractRpcCallback;
import com.github.doctrey.telegram.client.api.TLRequestMessagesGetMessagesViews;
import com.github.doctrey.telegram.client.listener.ListenerQueue;
import com.github.doctrey.telegram.client.listener.event.MessageViewedEvent;
import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import org.telegram.api.chat.channel.TLChannel;
import org.telegram.api.engine.TelegramApi;
import org.telegram.api.functions.channels.TLRequestChannelsReadHistory;
import org.telegram.api.input.chat.TLInputChannel;
import org.telegram.api.input.peer.TLInputPeerChannel;
import org.telegram.api.message.TLAbsMessage;
import org.telegram.api.message.TLMessage;
import org.telegram.api.peer.TLAbsPeer;
import org.telegram.api.peer.TLPeerChannel;
import org.telegram.tl.TLBool;
import org.telegram.tl.TLBoolTrue;
import org.telegram.tl.TLIntVector;

import java.util.List;

/**
 * Created by Soheil on 12/24/17.
 */
public class MessageService {

    private static final String TAG = "MessageService";

    private List<ChannelSubscriptionInfo> channelWhiteList;
    private ListenerQueue listenerQueue;
    private TelegramApi api;

    public MessageService(ListenerQueue listenerQueue, TelegramApi api) {
        this.listenerQueue = listenerQueue;
        this.api = api;
        channelWhiteList = new ChannelService().findAllPendingChannels();
    }

    public MessageService(ListenerQueue listenerQueue) {
        this(listenerQueue, null);
    }

    public MessageService() {
        this(null);
    }

    public void markChannelHistoryAsRead(TLAbsMessage absMessage, TLChannel channel) {
        if (absMessage instanceof TLMessage) {
            TLMessage message = (TLMessage) absMessage;
            TLAbsPeer toPeer = message.getToId();
            if (toPeer instanceof TLPeerChannel) {
                int channelId = toPeer.getId();
                if (channelWhiteList.stream().anyMatch(channelSubscriptionInfo -> channelSubscriptionInfo.getChannelId() == channelId)) {
                    TLInputChannel inputChannel = new TLInputChannel();
                    inputChannel.setChannelId(channel.getId());
                    inputChannel.setAccessHash(channel.getAccessHash());

                    TLRequestChannelsReadHistory readHistory = new TLRequestChannelsReadHistory();
                    readHistory.setMaxId(message.getId());
                    readHistory.setChannel(inputChannel);

                    // reading the history
                    api.doRpcCall(readHistory, new AbstractRpcCallback<TLBool>() {
                        @Override
                        public void onResult(TLBool result) {
                            if (result instanceof TLBoolTrue) {
                                TLInputPeerChannel inputPeerChannel = new TLInputPeerChannel();
                                inputPeerChannel.setChannelId(channel.getId());
                                inputPeerChannel.setAccessHash(channel.getAccessHash());

                                // incrementing the view
                                TLRequestMessagesGetMessagesViews messagesViews = new TLRequestMessagesGetMessagesViews();
                                TLIntVector intVector = new TLIntVector();
                                intVector.add(message.getId());
                                messagesViews.setPeer(inputPeerChannel);
                                messagesViews.setIncrement(true);
                                messagesViews.setId(intVector);
                                api.doRpcCall(messagesViews, new AbstractRpcCallback<TLIntVector>() {
                                    @Override
                                    public void onResult(TLIntVector result) {
                                        listenerQueue.publish(new MessageViewedEvent(inputPeerChannel, api));
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    public void setApi(TelegramApi api) {
        this.api = api;
    }
}
