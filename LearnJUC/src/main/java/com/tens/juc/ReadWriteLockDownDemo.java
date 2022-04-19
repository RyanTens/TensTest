package com.tens.juc;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockDownDemo {
    public static void main(String[] args) {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

        readLock.lock();
        System.out.println("---read");

//        readLock.unlock();

        writeLock.lock();
        System.out.println("hello");



//        writeLock.unlock();
//
//        readLock.unlock();
    }
}
