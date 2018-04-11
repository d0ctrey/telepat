package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.api.TLDbPersistence;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.updates.TLUpdatesState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by s_tayari on 4/11/2018.
 */
public class DbApiUpdateState extends TLDbPersistence<TLUpdatesState> {

    private String phoneNumber;

    public DbApiUpdateState(String phoneNumber) throws Exception {
        super(TLUpdatesState.class);
        this.phoneNumber = phoneNumber;
    }

    public void updateState(TLUpdatesState tlState) {
        getObj().setDate(tlState.getDate());
        getObj().setPts(tlState.getPts());
        getObj().setQts(tlState.getQts());
        getObj().setSeq(tlState.getSeq());
        getObj().setUnreadCount(tlState.getUnreadCount());
        try {
            updateData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected TLUpdatesState loadData() throws Exception {
        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT api_state FROM tl_phone_numbers WHERE phone_number = ?");
        ) {
            statement.setString(1, phoneNumber);
            try(ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    return (TLUpdatesState) rs.getObject(1);
                else
                    throw new Exception("Couldn't find a state for number " + phoneNumber);
            }
        }
    }

    @Override
    protected void updateData() throws Exception {
        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE tl_phone_numbers SET api_state = ? WHERE phone_number = ?");
        ) {
            statement.setObject(1, getObj());
            statement.setString(2, phoneNumber);
            statement.executeUpdate();
        }

    }
}
