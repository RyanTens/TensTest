package com.tens.channel;

import com.tens.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RecordChannelGroup {
    private Configuration configuration;

    private final int singleChannelListSize;

    private List<HashedRecordChannel> channelList = new ArrayList<>();
    private List<HashedRecordChannel> mirrorChannelList = new ArrayList<>();

    public RecordChannelGroup(Configuration configuration, int singleChannelListSize) {
        this.configuration = configuration;
        this.singleChannelListSize = singleChannelListSize;

        for (int i = 1; i <= singleChannelListSize; i++) {
            String nodeName = configuration.getNodeList().get(i);
            Properties nodeProp = configuration.getNodeInfo().get(nodeName);

            String mirrorNodeName = configuration.getNodeMapping().get(nodeName);
            Properties mirrorNodeProp = configuration.getMirrorNodeInfo().get(mirrorNodeName);

            HashedRecordChannel channel = new HashedRecordChannel(configuration, i, nodeName, nodeProp);
            HashedRecordChannel mirrorChannel = new HashedRecordChannel(configuration, -i, mirrorNodeName, mirrorNodeProp);

            channelList.add(channel);
            mirrorChannelList.add(mirrorChannel);
        }
    }

    public List<HashedRecordChannel> getChannelList() {
        return channelList;
    }

    public List<HashedRecordChannel> getMirrorChannelList() {
        return mirrorChannelList;
    }

    public void terminate() {
        for (int i = 0; i < this.singleChannelListSize; i++) {
            channelList.get(i).pushTerminate();
            mirrorChannelList.get(i).pushTerminate();
        }
    }

    public HashedRecordChannel getChannel(int hashcode) {
        return this.channelList.get(hashcode);
    }

    public HashedRecordChannel getMirrorChannel(int hashcode) {
        return this.mirrorChannelList.get(hashcode);
    }

    public int getHashSize() {
        return channelList.size();
    }
}
