package com.tens.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configuration {

    private final String taskId;

    private String filePath;

    private int channelCapacity = 1024 * 1024 * 10;

    private int bufferSize = 1024 * 1024 * 10;

    private String encoding = "UTF-8";

    private Character fieldDelimiter = ',';

    private boolean skipHead = false;

    private final Map<Integer, String> nodeList = new HashMap<>();

    private final Map<String, Properties> nodeInfo = new HashMap<>();

    private final Map<String, Properties> mirrorNodeInfo = new HashMap<>();

    private int distributeIndex = 1;

    public Configuration(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getDistributeIndex() {
        return distributeIndex;
    }

    public Map<String, Properties> getNodeInfo() {
        return this.nodeInfo;
    }

    //key=master, value=slaver
    private final Map<String, String> nodeMapping = new HashMap<>();

    public Map<String, Properties> getMirrorNodeInfo() {
        return mirrorNodeInfo;
    }

    public Map<String, String> getNodeMapping() {
        return nodeMapping;
    }

    public Map<Integer, String> getNodeList() {
        return nodeList;
    }

    public boolean isSkipHead() {
        return skipHead;
    }

    public String getEncoding() {
        return encoding;
    }

    public Character getFieldDelimiter() {
        return fieldDelimiter;
    }

    public int getChannelCapacity() {
        return channelCapacity;
    }

    public void setChannelCapacity(int channelCapacity) {
        this.channelCapacity = channelCapacity;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
