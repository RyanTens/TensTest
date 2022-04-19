package com.tens.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolDemo1 {
    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newFixedThreadPool(5);

        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            for (int i = 0; i < 20; i++) {
                executorService.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "办理业务");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }



    }
}
