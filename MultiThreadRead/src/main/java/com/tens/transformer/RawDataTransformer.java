package com.tens.transformer;

import com.tens.channel.RecordChannelGroup;
import com.tens.configuration.Configuration;
import com.tens.exchanger.BufferedRawDataExchanger;
import com.tens.util.PartitionByJavaHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RawDataTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(RawDataTransformer.class);

    private Configuration configuration;

    private RecordChannelGroup channelGroup;

    private HashMap<Integer, BufferedRawDataExchanger> recordSenders = new HashMap<>();

    private HashMap<Integer, BufferedRawDataExchanger> mirrirRecordSenders = new HashMap<>();

    private CountDownLatch countDownLatch;

    public RawDataTransformer(Configuration configuration, RecordChannelGroup channelGroup, CountDownLatch countDownLatch) {
        this.configuration = configuration;
        this.channelGroup = channelGroup;
        this.countDownLatch = countDownLatch;
    }

    public void startTransform(BufferedRawDataExchanger reciever) {
        int distributeIndex = configuration.getDistributeIndex();
        int partitionSize = channelGroup.getHashSize();
        generateRecordSender(channelGroup);
        String[] rowData;
        while ((rowData = reciever.getFromChannel()) != null && rowData.length != 0) {

            rowData[6] = rowData[6].substring(0,3);
            Integer hashcode = PartitionByJavaHash.calculate(rowData[distributeIndex], partitionSize);
            recordSenders.get(hashcode).sendToChannel(rowData);
            mirrirRecordSenders.get(hashcode).sendToChannel(rowData);
        }
        flush();
        reciever.terminate();
        LOG.info("tramsformer done!");
        countDownLatch.countDown();
    }

    private void flush() {
        for (int i = 0; i < channelGroup.getHashSize(); i++) {
            recordSenders.get(i).flush();
            mirrirRecordSenders.get(i).flush();
        }
    }

    private void generateRecordSender(RecordChannelGroup channelGroup) {
        for (int i = 0; i < channelGroup.getHashSize(); i++) {
            recordSenders.put(i, new BufferedRawDataExchanger(channelGroup.getChannel(i), configuration));
            mirrirRecordSenders.put(i, new BufferedRawDataExchanger(channelGroup.getMirrorChannel(i), configuration));
        }
    }


}
