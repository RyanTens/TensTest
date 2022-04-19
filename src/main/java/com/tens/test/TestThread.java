package com.tens.test;

public class TestThread {
    private int value;

    /**
     * NotThreadSafe
     * @return
     */
    public int getNext1() {
        return value++;
    }

    /**
     * ThreadSafe
     * @return
     */
    public synchronized int getNext2() {
        return value++;
    }
}
