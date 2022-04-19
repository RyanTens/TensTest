package com.tens.sync;

public class Ticket {
    private int number = 30;

    public synchronized void sale() {
        if (number > 0) {
            System.out.println(Thread.currentThread().getName() + ":：卖出--" + (number--) + " 剩下：" + number);
        }
    }
}
