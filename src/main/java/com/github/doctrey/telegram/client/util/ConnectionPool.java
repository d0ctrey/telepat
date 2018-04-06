package com.github.doctrey.telegram.client.util;

import org.postgresql.ds.PGPoolingDataSource;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Soheil on 2/15/18.
 */
public class ConnectionPool {

    private DataSource dataSource;

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static class DatasourceLoader {
        private static final ConnectionPool INSTANCE = new ConnectionPool();
    }

    private ConnectionPool() {
        if (DatasourceLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated.");
        }

        URI dbUri;
        try {
            dbUri = new URI(System.getenv("DATABASE_URL"));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
//        String username = dbUri.getUserInfo().split(":")[0];
//        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setUrl(dbUrl);
//        source.setUser(username);
//        source.setPassword(password);
        source.setMaxConnections(20);
        this.dataSource = source;
    }

    public static ConnectionPool getInstance() {
        return DatasourceLoader.INSTANCE;
    }
}
