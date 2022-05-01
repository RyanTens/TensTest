package com.tens.channel;

import com.tens.configuration.Configuration;

import java.util.Properties;

public class HashedRecordChannel extends RawDataChannel {

    //channel编号 非hash值
    private int channelNum = Integer.MIN_VALUE;

    private String username;
    private String password;
    private String jdbcUrl;
    private String nodeName;
    private String dbType;

    public HashedRecordChannel(Configuration configuration, final int chnnelNum, final String nodeName, final Properties nodeProp) {
        super(configuration);
        this.channelNum = channelNum;
        this.nodeName = nodeName;
        bindConnection(nodeProp);
    }

    public void bindConnection(final Properties nodeProp) {
//        this.username = nodeProp.getProperty("username");
//        this.password = nodeProp.getProperty("password");
//        this.jdbcUrl = nodeProp.getProperty("jdbcUrl");
        this.dbType = nodeProp.getProperty("dbType");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public String getDbType() {
        return dbType;
    }
}
