package com.tens.juc;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        //创建 CountDownLatch对象，设置初始值
        CountDownLatch countDownLatch = new CountDownLatch(6);

        for (int i = 1; i <= 6; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "号同学离开了教室");
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }

        //阻塞
        countDownLatch.await();

        System.out.println(Thread.currentThread().getName() + "班长锁门了！");
    }
}
