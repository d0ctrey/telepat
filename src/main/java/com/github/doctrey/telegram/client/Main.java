package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.util.ConnectionPool;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Soheil on 4/13/18.
 */
public class Main {

    private static final String PHONE_NUMBER = "8615504466140";

    public static void main(String[] args) throws Exception {
        try(
                Connection conn = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = conn.prepareStatement("INSERT INTO tl_channels(invite_link, plan_start_date, plan_expire_date, plan_active) VALUES (?, ?, ?, ?)")
        ) {
            statement.setString(1, "https://t.me/joinchat/AAAAAFAUnmMDCYZWBwXixQ");
            statement.setDate(2, new Date(new java.util.Date().getTime()));
            statement.setDate(3, new Date(new java.util.Date().getTime() + 86400000));
            statement.setInt(4, 1);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
