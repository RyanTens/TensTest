package com.tens.channel;

import com.tens.configuration.Configuration;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RawDataChannel {

    private int capacity;

    private int bufferSize = 0;

    private int byteCapacity = 0;

    private AtomicInteger memoryBytes = new AtomicInteger(0);

    private ArrayBlockingQueue<String []> queue = null;

    private ReentrantLock lock;

    private Condition notInsufficient, notEmpty;

    protected volatile boolean isClosed = false;

    public RawDataChannel(Configuration configuration) {
        this.byteCapacity = configuration.getChannelCapacity();
        this.queue = new ArrayBlockingQueue<>(configuration.getChannelCapacity());
        this.bufferSize = configuration.getBufferSize();

        lock = new ReentrantLock();
        notInsufficient = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public void close() {
        this.isClosed = true;
        try {
            this.queue.put(new String[]{});
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void clear() {
        this.queue.clear();
    }

    protected void doPush(String[] rawData) {
        try {
            this.queue.put(rawData);
            memoryBytes.addAndGet(getRawDataMemorySize(rawData));

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void doPushAll(Collection<String[]> rs) {
        try {
            lock.lockInterruptibly();
            int bytes = getRawDataCollectionBytes(rs);
            while (memoryBytes.get() + bytes > this.byteCapacity || rs.size() > this.queue.remainingCapacity()) {
                notInsufficient.await(200L, TimeUnit.MILLISECONDS);
            }
            this.queue.addAll(rs);
            memoryBytes.addAndGet(bytes);
            notEmpty.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    protected String[] doPull() {
        try {
            String[] r = this.queue.take();
            memoryBytes.addAndGet(-getRawDataMemorySize(r));
            return r;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    public void pullAll(Collection<String[]> rs) {
        assert rs != null;
        rs.clear();
        try {
            lock.lockInterruptibly();
            while (this.queue.drainTo(rs, bufferSize) <= 0) {
                notEmpty.await(200L, TimeUnit.MILLISECONDS);
            }
            int bytes = getRawDataCollectionBytes(rs);
            memoryBytes.addAndGet(-bytes);
            notInsufficient.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
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

    public void pushAll(final Collection<String[]> rs) {
        Validate.notNull(rs);
        Validate.noNullElements(rs);
        this.doPushAll(rs);
    }

    public void pushTerminate() {
        this.doPush(new String[0]);
    }
}
