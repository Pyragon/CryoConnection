package com.cryo.entities;

import org.apache.commons.pool.BasePoolableObjectFactory;

import java.sql.DriverManager;

public class ConnectionPoolFactory extends BasePoolableObjectFactory {

    private String host;
    private int port;
    private String schema;
    private String user;
    private String password;
    private String server;

    public ConnectionPoolFactory(String host, int port, String schema,
                                 String user, String password) {
        this(host, port, schema, user, password, null);
    }

    public ConnectionPoolFactory(String host, int port, String schema, String user, String password, String server) {
        this.host = host;
        this.port = port;
        this.schema = schema;
        this.user = user;
        this.password = password;
        this.server = server;
    }

    @Override
    public Object makeObject() throws Exception {
        String url = "jdbc:"+server+"://" + host + ":" + port + "/" + schema + "?autoReconnectForPools=true&characterEncoding=latin1&useConfigs=maxPerformance";
        return DriverManager.getConnection(url, user, password);
    }
}
