package com.tens.juc;

public class Juc {
    public static void main(String[] args) {
        Thread t1 = new Thread(() ->
                System.out.println(Thread.currentThread().getName() + "::" + Thread.currentThread().isDaemon()));
        t1.start();



    }
}
