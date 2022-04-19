package com.tens.sync;

public class Share {
    private int number  = 0;

    public synchronized void incr() throws InterruptedException {
        while (number != 0) {
            this.wait();
        }

        number ++;
        System.out.println(Thread.currentThread().getName() + ":::" + number);
        this.notifyAll();
    }

    public synchronized void decr() throws InterruptedException {
        while (number != 1) {
            this.wait();
        }

        number--;
        System.out.println(Thread.currentThread().getName() + ":::" + number);
        this.notifyAll();
    }
}
