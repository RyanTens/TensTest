package com.tens.completable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "completableFuture1");
        });

        completableFuture1.get();

        CompletableFuture<Integer> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "completableFuture2");
            int i = 1 / 0;
            return 200;
        });

        completableFuture2.whenComplete((u, t) -> {
            System.out.println("---u=" + u);
            System.out.println("---t=" + t);

        }).get();
    }
}
