package com.tens;

import com.tens.configuration.Configuration;
import com.tens.container.SingleThreadContainer;

import java.util.Map;
import java.util.Properties;

public class Start {
    public static void main(String[] args) {
        Configuration configuration = new Configuration("test1234");
        configuration.setFilePath("/tmp/mockdata");
        Map<String, String> nodeMapping = configuration.getNodeMapping();
        nodeMapping.put("node1", "mnode2");
        nodeMapping.put("node2", "mnode3");
        nodeMapping.put("node3", "mnode1");

        Map<String, Properties> nodeInfo = configuration.getNodeInfo();
        Map<String, Properties> mirrorNodeInfo = configuration.getMirrorNodeInfo();
        Properties properties = new Properties();
        properties.put("dbtype", "pg");
        nodeInfo.put("node1", properties);
        nodeInfo.put("node2", properties);
        nodeInfo.put("node3", properties);

        mirrorNodeInfo.put("mnode1", properties);
        mirrorNodeInfo.put("mnode2", properties);
        mirrorNodeInfo.put("mnode3", properties);

        Map<Integer, String> nodeList = configuration.getNodeList();

        nodeList.put(1, "node1");
        nodeList.put(2, "node2");
        nodeList.put(3, "node3");

        SingleThreadContainer container = new SingleThreadContainer(configuration);
        try {
            container.prepare();
            container.init();
            container.start();
            container.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
