package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.api.TLDbPersistence;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.engine.Logger;
import org.telegram.api.updates.TLUpdatesState;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by s_tayari on 4/11/2018.
 */
public class DbApiUpdateState extends TLDbPersistence<TLUpdatesState> {

    private static final String TAG = "DbApiUpdateState";

    public DbApiUpdateState(String phoneNumber) {
        super(phoneNumber, TLUpdatesState.class);
    }

    public void updateState(TLUpdatesState tlState) {
        getObj().setDate(tlState.getDate());
        getObj().setPts(tlState.getPts());
        getObj().setQts(tlState.getQts());
        getObj().setSeq(tlState.getSeq());
        getObj().setUnreadCount(tlState.getUnreadCount());
        updateData();
    }

    @Override
    protected TLUpdatesState loadData() {
        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT api_state FROM tl_phone_numbers WHERE phone_number = ?");
        ) {
            statement.setString(1, phoneNumber);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next())
                    try (InputStream is = rs.getBinaryStream(1)) {
                        if (is != null)
                            try (ObjectInputStream ois = new ObjectInputStream(is)) {
                                return (TLUpdatesState) ois.readObject();
                            }
                        else
                            return null;
                    } catch (IOException | ClassNotFoundException e) {
                        Logger.w(TAG, "Failed to read the persisted update status from DB.");
                        return null;
                    }
                else {
                    Logger.w(TAG, "Couldn't find any state for number " + phoneNumber);
                    return null;
                }
            }
        } catch (SQLException e) {
            Logger.e(TAG, e);
            return null;
        }
    }

    @Override
    protected void updateData() {
        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE tl_phone_numbers SET api_state = ? WHERE phone_number = ?");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(getObj());
            try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                statement.setBinaryStream(1, bais);
                statement.setString(2, phoneNumber);
                statement.executeUpdate();
            }

        } catch (SQLException | IOException e) {
            Logger.w(TAG, "Failed to read the persisted storage from DB.");
        }

    }
}
