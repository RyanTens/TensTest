package com.tens.util;

import java.util.concurrent.atomic.AtomicLong;

public class CounterUtil {

    private AtomicLong time = new AtomicLong(0);

    private AtomicLong size = new AtomicLong(0);

    private CounterUtil() {

    }

    private static class CounterUtilInstance {
        private static final CounterUtil INSTANCE = new CounterUtil();
    }

    public static CounterUtil getInstance() {
        return CounterUtilInstance.INSTANCE;
    }

    public void addTime(long time) {
        this.time.getAndAdd(time);
    }

    public void addSize(long size) {
        this.size.getAndAdd(size);
    }

    public AtomicLong getTime() {
        return time;
    }

    public AtomicLong getSize() {
        return size;
    }
}
