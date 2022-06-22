package com.tens.learn.guava;

import com.google.common.util.concurrent.AbstractExecutionThreadService;

import java.util.concurrent.TimeUnit;

public class ServiceDemo extends AbstractExecutionThreadService {

    private volatile boolean isRunning = true;

    @Override
    protected void run() throws Exception {
        TimeUnit.SECONDS.sleep(5);
        System.out.println("AbstractExecutionThreadService do something...");
    }

    @Override
    protected void triggerShutdown() {

    }
}
