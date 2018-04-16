package com.github.doctrey.telegram.client.facade;

import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionInfo;
import com.github.doctrey.telegram.client.subscription.ChannelSubscriptionStatus;
import com.github.doctrey.telegram.client.subscription.SubscriptionPlan;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.TelegramApi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by s_tayari on 4/16/2018.
 */
public class ChannelService {

    private static final String TAG = "ChannelService";

    private TelegramApi api;

    public ChannelService() {
        this(null);
    }

    public ChannelService(TelegramApi api) {
        this.api = api;
    }

    public void markChannel(int id, ChannelSubscriptionStatus status) {

    }

    public List<ChannelSubscriptionInfo> findAllPendingChannels() {
        List<ChannelSubscriptionInfo> subscriptionInfoList = new ArrayList<>();
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM tl_channels WHERE status = ?")){
            statement.setInt(1, ChannelSubscriptionStatus.VERIFIED.getCode());
            try (ResultSet rs = statement.executeQuery()){
                while (rs.next()) {
                    ChannelSubscriptionInfo info = new ChannelSubscriptionInfo();
                    info.setId(rs.getInt(1));
                    info.setInviteLink(rs.getString(2));
                    info.setChannelId(rs.getInt(3));
                    info.setPlanId(rs.getInt(4));
                    info.setPlanStart(rs.getDate(5));
                    info.setPlanExpiration(rs.getDate(6));
                    info.setMemberCount(rs.getInt(7));
                    info.setSubscriptionStatus(ChannelSubscriptionStatus.withCode(rs.getInt(8)));
                    subscriptionInfoList.add(info);
                }
            }

            return subscriptionInfoList;
        } catch (SQLException e) {
            Logger.e(TAG, e);
            return Collections.emptyList();
        }
    }

    public SubscriptionPlan findPlanForChannel(int channelId) {
        return null;
    }

    public void incrementChannelMember(int channelId) {
        try(Connection connection = ConnectionPool.getInstance().getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE tl_channels SET member_count = member_count + 1 WHERE id = ?")
        ) {
            statement.setInt(1, channelId);
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.e(TAG, e);
        }
    }

    public void joinChannel(int channelId, String inviteLink) {

        incrementChannelMember(channelId);
    }
}
