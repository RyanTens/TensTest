package com.tens.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LShare {
    private int number = 0;

    private Lock lock = new ReentrantLock(true);
    private Condition condition = lock.newCondition();

    public void incr() throws InterruptedException {
        lock.lock();
        try {
            while (number != 0) {
                condition.await();
            }

            number++;
            System.out.println(Thread.currentThread().getName() + ":::" + number);

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void decr() throws InterruptedException {
        lock.lock();
        try {
            while (number != 1) {
                condition.await();
            }

            number--;
            System.out.println(Thread.currentThread().getName() + ":::" + number);

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
