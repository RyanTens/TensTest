package com.tens;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestPG {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://192.168.10.101:5432/postgres";
        String user = "postgres";
        String password = "123456";
        String tablename = "tb_evt_test";
        String sql = String.format("COPY %s FROM STDIN (FORMAT 'csv')", tablename);
        Connection connection = DriverManager.getConnection(url, user, password);
        CopyManager copyManager = new CopyManager((BaseConnection) connection);
        InputStream inputStream = new FileInputStream(new File("D:\\tmp\\mockdata\\g2021-01-01_9efa0e9aeaa34ef295494c7127da79b9"));
        copyManager.copyIn(sql, inputStream);
        inputStream.close();
    }
}
