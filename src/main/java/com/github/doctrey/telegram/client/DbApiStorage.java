package com.github.doctrey.telegram.client;

import com.github.doctrey.telegram.client.api.*;
import com.github.doctrey.telegram.client.util.ConnectionPool;
import org.telegram.api.TLConfig;
import org.telegram.api.TLDcOption;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.engine.Logger;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.api.user.TLUser;
import org.telegram.mtproto.state.AbsMTProtoState;
import org.telegram.mtproto.state.ConnectionInfo;
import org.telegram.mtproto.state.KnownSalt;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by s_tayari on 4/11/2018.
 */
public class DbApiStorage extends TLDbPersistence<TLStorage> implements AbsApiState {

    private static final String TAG = "DbApiStorage";

    public DbApiStorage(String phoneNumber) {
        super(phoneNumber, TLStorage.class);
        if (getObj().getDcInfos().size() == 0) {
            getObj().getDcInfos().add(new TLDcInfo(1, DcInitialConfig.ADDRESS, DcInitialConfig.PORT, 0));
        }
    }

    @Override
    protected TLStorage loadData() {
        try (
                Connection connection = ConnectionPool.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT api_storage FROM tl_phone_numbers WHERE phone_number = ?")
        ) {
            statement.setString(1, phoneNumber);
            try(ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    try(InputStream is = rs.getBinaryStream(1)) {
                        if(is != null)
                            try(ObjectInputStream ois = new ObjectInputStream(is)) {
                                return (TLStorage) ois.readObject();
                            }
                        else
                            return null;
                    } catch (IOException | ClassNotFoundException e) {
                        Logger.w(TAG, "Failed to read the persisted storage from DB.");
                        return null;
                    }
                } else {
                    Logger.w(TAG, "Failed to find any persisted storage.");
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
                PreparedStatement statement = connection.prepareStatement("UPDATE tl_phone_numbers SET api_storage = ? WHERE phone_number = ?");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(getObj());
            try(ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                statement.setBinaryStream(1, bais);
                statement.setString(2, phoneNumber);
                statement.executeUpdate();
            }
        } catch (SQLException | IOException e) {
            Logger.e(TAG, e);
        }
    }

    public int[] getKnownDc() {
        HashSet<Integer> dcs = new HashSet<Integer>();
        for (TLDcInfo dcInfo : getObj().getDcInfos()) {
            dcs.add(dcInfo.getDcId());
        }
        Integer[] dcsArray = dcs.toArray(new Integer[0]);
        int[] res = new int[dcs.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = dcsArray[i];
        }
        return res;
    }

    private TLKey findKey(int dc) {
        for (TLKey key : getObj().getKeys()) {
            if (key.getDcId() == dc) {
                return key;
            }
        }
        return null;
    }

    public synchronized boolean isAuthenticated() {
        return isAuthenticated(getPrimaryDc());
    }

    public synchronized void doAuth(TLAuthorization authorization) {
        Logger.d(TAG, "doAuth1");
        TLKey key = findKey(getPrimaryDc());
        key.setAuthorised(true);
        getObj().setUid(authorization.getUser().getId());
        getObj().setPhone(((TLUser) authorization.getUser()).getPhone());
        updateData();
    }

    public synchronized void doAuth(int uid, String phone) {
        Logger.d(TAG, "doAuth2");
        TLKey key = findKey(getPrimaryDc());
        key.setAuthorised(true);
        getObj().setUid(uid);
        getObj().setPhone(phone);
        updateData();
    }

    @Override
    public synchronized int getPrimaryDc() {
        return getObj().getPrimaryDc();
    }

    @Override
    public synchronized void setPrimaryDc(int dc) {
        Logger.d(TAG, "setPrimaryDc dc #" + dc);
        getObj().setPrimaryDc(dc);
        updateData();
    }

    @Override
    public synchronized boolean isAuthenticated(int dcId) {
        TLKey key = findKey(dcId);
        return (key != null && key.isAuthorised());
    }

    @Override
    public synchronized void setAuthenticated(int dcId, boolean auth) {
        Logger.d(TAG, "setAuthenticated dc #" + dcId + ": " + auth);
        TLKey key = findKey(dcId);
        key.setAuthorised(auth);
        updateData();
    }

    @Override
    public synchronized void updateSettings(TLConfig config) {
        int version = 0;
        for (TLDcInfo info : getObj().getDcInfos()) {
            version = Math.max(version, info.getVersion());
        }

        boolean hasUpdates = false;
        for (TLDcOption option : config.getDcOptions()) {
            boolean contains = false;
            for (TLDcInfo info : getObj().getDcInfos().toArray(new TLDcInfo[0])) {
                if (info.getAddress().equals(option.getIpAddress()) && info.getPort() == option.getPort() && info.getDcId() == option.getId() && info.getVersion() == version) {
                    contains = true;
                    break;
                }
            }

            if (!contains) {
                hasUpdates = true;
            }
        }

        if (!hasUpdates) {
            Logger.d(TAG, "No updates for DC");
            return;
        }

        int nextVersion = version + 1;
        for (TLDcOption option : config.getDcOptions()) {
            for (TLDcInfo info : getObj().getDcInfos().toArray(new TLDcInfo[0])) {
                if (info.getAddress().equals(option.getIpAddress()) && info.getDcId() == option.getId()) {
                    getObj().getDcInfos().remove(info);
                }
            }
            if(!option.isIPV6() || (option.isIPV6() && Boolean.valueOf(System.getenv("TL_ALLOW_IPV6"))))
                getObj().getDcInfos().add(new TLDcInfo(option.getId(), option.getIpAddress(), option.getPort(), nextVersion));
        }
        updateData();
    }

    public synchronized void updateDCInfo(int dcId, String ip, int port) {
        for (TLDcInfo info : getObj().getDcInfos().toArray(new TLDcInfo[0])) {
            if (info.getAddress().equals(ip) && info.getPort() == port && info.getDcId() == dcId) {
                getObj().getDcInfos().remove(info);
            }
        }

        int version = 0;
        for (TLDcInfo info : getObj().getDcInfos()) {
            version = Math.max(version, info.getVersion());
        }

        getObj().getDcInfos().add(new TLDcInfo(dcId, ip, port, version));
        updateData();
    }

    @Override
    public synchronized byte[] getAuthKey(int dcId) {
        TLKey key = findKey(dcId);
        return key != null ? key.getAuthKey() : null;
    }

    @Override
    public synchronized void putAuthKey(int dcId, byte[] authKey) {
        Logger.d(TAG, "putAuthKey dc #" + dcId);
        TLKey key = findKey(dcId);
        if (key != null) {
            return;
        }
        getObj().getKeys().add(new TLKey(dcId, authKey));
        updateData();
    }

    @Override
    public ConnectionInfo[] getAvailableConnections(int dcId) {
        ArrayList<TLDcInfo> infos = new ArrayList<TLDcInfo>();
        int maxVersion = 0;
        for (TLDcInfo info : getObj().getDcInfos()) {
            if (info.getDcId() == dcId) {
                infos.add(info);
                maxVersion = Math.max(maxVersion, info.getVersion());
            }
        }

        ArrayList<ConnectionInfo> res = new ArrayList<ConnectionInfo>();

        // Maximum version addresses
        HashMap<String, DcAddress> mainAddresses = new HashMap<String, DcAddress>();
        for (TLDcInfo i : infos) {
            if (i.getVersion() != maxVersion) {
                continue;
            }

            if (mainAddresses.containsKey(i.getAddress())) {
                mainAddresses.get(i.getAddress()).ports.put(i.getPort(), 1);
            } else {
                DcAddress address = new DcAddress();
                address.ports.put(i.getPort(), 1);
                address.host = i.getAddress();
                mainAddresses.put(i.getAddress(), address);
            }
        }

        for (DcAddress address : mainAddresses.values()) {
            address.ports.put(443, 2);
            address.ports.put(80, 1);
            address.ports.put(25, 0);
        }

        HashMap<Integer, HashMap<String, DcAddress>> otherAddresses = new HashMap<Integer, HashMap<String, DcAddress>>();

        for (TLDcInfo i : infos) {
            if (i.getVersion() == maxVersion) {
                continue;
            }

            if (!otherAddresses.containsKey(i.getVersion())) {
                otherAddresses.put(i.getVersion(), new HashMap<String, DcAddress>());
            }

            HashMap<String, DcAddress> addressHashMap = otherAddresses.get(i.getVersion());

            if (addressHashMap.containsKey(i.getAddress())) {
                addressHashMap.get(i.getAddress()).ports.put(i.getPort(), 1);
            } else {
                DcAddress address = new DcAddress();
                address.ports.put(i.getPort(), 1);
                address.host = i.getAddress();
                addressHashMap.put(i.getAddress(), address);
            }
        }

        for (Integer version : otherAddresses.keySet()) {
            for (DcAddress address : otherAddresses.get(version).values()) {
                if (mainAddresses.containsKey(address.host)) {
                    continue;
                }
                address.ports.put(443, 2);
                address.ports.put(80, 1);
                address.ports.put(25, 0);
            }
        }


        // Writing main addresses
        int index = 0;

        for (DcAddress address : mainAddresses.values()) {
            for (Integer port : address.ports.keySet()) {
                int priority = maxVersion + address.ports.get(port);
                res.add(new ConnectionInfo(index++, priority, address.host, port));
            }
        }

        // Writing other addresses

        for (Integer version : otherAddresses.keySet()) {
            for (DcAddress address : otherAddresses.get(version).values()) {
                for (Integer port : address.ports.keySet()) {
                    int priority = version + address.ports.get(port);
                    res.add(new ConnectionInfo(index++, priority, address.host, port));
                }
            }
        }

        Logger.d(TAG, "Created connections for dc #" + dcId);
        for (ConnectionInfo c : res) {
            Logger.d(TAG, "Connection: #" + c.getId() + " " + c.getAddress() + ":" + c.getPort() + " at " + c.getPriority());
        }

        return res.toArray(new ConnectionInfo[0]);
    }

    private synchronized void writeKnownSalts(int dcId, KnownSalt[] salts) {
        TLKey key = findKey(dcId);
        key.getSalts().clear();
        for (int i = 0; i < salts.length; i++) {
            key.getSalts().add(new TLLastKnownSalt(salts[i].getValidSince(), salts[i].getValidUntil(), salts[i].getSalt()));
        }
        updateData();
    }

    private synchronized KnownSalt[] readKnownSalts(int dcId) {
        TLKey key = findKey(dcId);
        KnownSalt[] salts = new KnownSalt[key.getSalts().size()];
        for (int i = 0; i < salts.length; i++) {
            TLLastKnownSalt sourceSalt = key.getSalts().get(i);
            salts[i] = new KnownSalt(sourceSalt.getValidSince(), sourceSalt.getValidUntil(), sourceSalt.getSalt());
        }
        return salts;
    }

    @Override
    public synchronized AbsMTProtoState getMtProtoState(final int dcId) {
        return new AbsMTProtoState() {

            private KnownSalt[] knownSalts = null;

            @Override
            public byte[] getAuthKey() {
                return DbApiStorage.this.getAuthKey(dcId);
            }

            @Override
            public ConnectionInfo[] getAvailableConnections() {
                return DbApiStorage.this.getAvailableConnections(dcId);
            }

            @Override
            public KnownSalt[] readKnownSalts() {
                if (knownSalts == null) {
                    knownSalts = DbApiStorage.this.readKnownSalts(dcId);
                }
                return knownSalts;
            }

            @Override
            protected void writeKnownSalts(KnownSalt[] salts) {
                DbApiStorage.this.writeKnownSalts(dcId, salts);
                knownSalts = null;
            }
        };
    }

    @Override
    public synchronized void resetAuth() {
        Logger.d(TAG, "resetAuth");
        for (TLKey key : getObj().getKeys()) {
            key.setAuthorised(false);
        }
        getObj().setAuthorized(false);
        getObj().setUid(0);
        updateData();
    }

    @Override
    public synchronized void reset() {
        Logger.d(TAG, "reset");
        getObj().getKeys().clear();
        getObj().setAuthorized(false);
        getObj().setUid(0);
        updateData();
    }

    @Override
    public int getUserId() {
        return 0;
    }

    private class DcAddress {
        public String host;
        public HashMap<Integer, Integer> ports = new HashMap<Integer, Integer>();
    }
}
