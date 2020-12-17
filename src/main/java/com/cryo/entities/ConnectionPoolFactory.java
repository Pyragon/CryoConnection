package com.cryo.entities;

import org.apache.commons.pool.BasePoolableObjectFactory;

import java.sql.DriverManager;

public class ConnectionPoolFactory extends BasePoolableObjectFactory {

    private String host;
    private int port;
    private String schema;
    private String user;
    private String password;

    public ConnectionPoolFactory(String host, int port, String schema,
                                      String user, String password) {
        this.host = host;
        this.port = port;
        this.schema = schema;
        this.user = user;
        this.password = password;
    }

    @Override
    public Object makeObject() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").getConstructor().newInstance();
        String url = "jdbc:mysql://" + host + ":" + port + "/" + schema + "?autoReconnectForPools=true&characterEncoding=latin1&useConfigs=maxPerformance";
        return DriverManager.getConnection(url, user, password);
    }
}
