package com.tens.lock;

import java.util.concurrent.locks.ReentrantLock;

public class LTicket {
    private int number = 30;

    //创建可重入锁
    private final ReentrantLock lock = new ReentrantLock(true);

    public void sale() {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + ":：卖出--" + (number--) + " 剩下：" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }
}
