package com.tens.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 */
public class TestThread2 {
    public static final String URL = "xxx";
    private static ThreadLocal<Connection> connectionHolder
            = new ThreadLocal<Connection>() {
        public Connection initialValue() {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(URL);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return connection;
        }
    };

    public static Connection getConnection() {
        return connectionHolder.get();
    }
}
