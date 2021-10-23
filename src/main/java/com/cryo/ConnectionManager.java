package com.cryo;

import com.cryo.entities.ConnectionPoolFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class ConnectionManager {

    private HashMap<String, ObjectPool> factories;
    private static Properties properties;

    @Getter
    private static Gson gson;

    public ConnectionManager() {
        gson = buildGson();
        loadProperties();
        factories = new HashMap<>();
    }

    public DBConnection getConnection(String schema) {
        try {
            if(factories.containsKey(schema))
                return new DBConnection(schema, factories.get(schema));
            return createConnectionPool(schema);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBConnection createConnectionPool(String schema) {
        String host = properties.getProperty("db-host");
        String port = properties.getProperty("db-port");
        String user = properties.getProperty("db-user");
        String pass = properties.getProperty("db-pass");
        String server = "mysql";
        if(properties.contains("db-server"))
            server = properties.getProperty("db-server");

        PoolableObjectFactory objectFactory = new ConnectionPoolFactory(host, Integer.parseInt(port), schema, user, pass, server);
        GenericObjectPool.Config config = new GenericObjectPool.Config();
        config.maxActive = 10;
        config.testOnBorrow = true;
        config.testWhileIdle = true;
        config.timeBetweenEvictionRunsMillis = 10000;
        config.minEvictableIdleTimeMillis = 60000;

        GenericObjectPoolFactory genericObjectPoolFactory = new GenericObjectPoolFactory(objectFactory, config);
        ObjectPool pool = genericObjectPoolFactory.createPool();
        factories.put(schema, pool);
        try {
            return new DBConnection(schema, pool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Gson buildGson() {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setVersion(1.0)
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();
        return gson;
    }

    public static void loadProperties() {
        File file = new File("data/props.json");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = reader.readLine()) != null)
                builder.append(line);
            String json = builder.toString();
            properties = getGson().fromJson(json, Properties.class);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
