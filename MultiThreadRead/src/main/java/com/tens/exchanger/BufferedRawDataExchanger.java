package com.tens.exchanger;

import com.tens.channel.RawDataChannel;
import com.tens.configuration.Configuration;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BufferedRawDataExchanger {

    private final RawDataChannel channel;

    private final Configuration configuration;

    private int bufferSize;

    private final List<String[]> buffer;

    protected final int byteCapacity;

    private final AtomicInteger memoryBytes = new AtomicInteger(0);

    private int bufferIndex = 0;

    private volatile boolean shutdown = false;


    public BufferedRawDataExchanger(RawDataChannel channel, Configuration configuration) {
        assert channel != null;
        assert configuration != null;

        this.channel = channel;
        this.configuration = configuration;
        this.bufferSize = configuration.getBufferSize();
        this.buffer = new ArrayList<>(bufferSize);
        this.byteCapacity = configuration.getChannelCapacity();
    }

    public void sendToChannel(String[] record) {
        if (shutdown) {
            return;
        }

        int recordBytes = getRawDataMemorySize(record);

        Validate.notNull(record, "record 不能为空");
        if (recordBytes> this.byteCapacity) {
            System.out.println("一条数据大于容量大小！");
            return;
        }

        boolean isFull = this.bufferIndex >= bufferSize || this.memoryBytes.get() + recordBytes > this.byteCapacity;
        if (isFull) {
            flush();
        }

        this.buffer.add(record);
        this.bufferIndex++;
        memoryBytes.addAndGet(recordBytes);
    }

    public void flush() {
        if (shutdown) {
            return;
        }
        this.channel.pushAll(this.buffer);
        this.buffer.clear();
        this.bufferIndex = 0;
        this.memoryBytes.set(0);
    }

    public void terminate() {
        if (shutdown) {
            return;
        }

        flush();
        this.channel.pushTerminate();
    }

    public String[] getFromChannel() {
        if (shutdown) {
            return new String[]{};
        }

        boolean isEmpty = this.bufferIndex >= this.buffer.size();
        if (isEmpty) {
            receive();
        }
        String[] record = this.buffer.get(this.bufferIndex++);
        if (record.length == 0) {
            record = null;
        }
        return record;
    }

    private void receive() {
        this.channel.pullAll(this.buffer);
        this.bufferIndex = 0;
        this.bufferSize = this.buffer.size();
    }

    private int getRawDataCollectionBytes(Collection<String[]> rs) {
        int bytes = 0;
        for (String[] r: rs) {
            bytes += getRawDataMemorySize(r);
        }

        return bytes;
    }

    private int getRawDataMemorySize(String[] rawData) {
        int byteSize = 0;
        for (String s: rawData) {
            byteSize += 24 + 16 + s.length() * 2;
        }

        return byteSize;
    }
}
