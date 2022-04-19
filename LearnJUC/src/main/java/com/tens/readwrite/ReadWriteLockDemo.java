package com.tens.readwrite;

public class ReadWriteLockDemo {


    public static void main(String[] args) {
        MyCache myCache = new MyCache();

        for (int i = 1; i <= 5 ; i++) {
            final int num = i;
            new Thread(() -> {
                myCache.put(num + "", num+"");
            }, String.valueOf(i)).start();
        }

        for (int i = 1; i <= 5 ; i++) {
            final int num = i;
            new Thread(() -> {
                myCache.get(num + "");
            }, String.valueOf(i)).start();
        }
    }
}
