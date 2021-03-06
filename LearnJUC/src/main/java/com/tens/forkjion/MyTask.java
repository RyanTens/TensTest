package com.tens.forkjion;

import java.util.concurrent.RecursiveTask;

public class MyTask extends RecursiveTask<Integer> {
    private static final Integer VALUE = 10;
    private int begin;
    private int end;
    private int result;

    public MyTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }


    @Override
    protected Integer compute() {
        if ((end - begin) <= VALUE) {
            for (int i = begin; i <= end ; i++) {
                result = result + i;
            }
        } else {
            int middle = (begin + end) / 2;

            MyTask task01 = new MyTask(begin, middle);
            MyTask task02 = new MyTask(middle + 1, end);

            task01.fork();
            task02.fork();

            result = task01.join() + task02.join();

        }
        return result;
    }
}
