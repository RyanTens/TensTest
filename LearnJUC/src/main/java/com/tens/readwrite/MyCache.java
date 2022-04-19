package com.tens.readwrite;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyCache {
    private volatile Map<String, Object> map = new HashMap<>();

    private ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public void put(String key, Object value) {
        rwLock.writeLock().lock();
        System.out.println(Thread.currentThread().getName() + "正在写操作:" +key);
        map.put(key, value);
        System.out.println(Thread.currentThread().getName() + "已写完:" +key);
        try {
            TimeUnit.MICROSECONDS.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwLock.writeLock().unlock();
        }



    }

    public Object get(String key) {
        rwLock.readLock().lock();
        Object result = null;
        System.out.println(Thread.currentThread().getName() + "正在读操作:" +key);
        try {
            TimeUnit.MICROSECONDS.sleep(300);
            result = map.get(key);

            System.out.println(Thread.currentThread().getName() + "已取出:" +key);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rwLock.readLock().unlock();
        }

        return result;
    }
}
