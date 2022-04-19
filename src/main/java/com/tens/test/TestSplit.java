package com.tens.test;

public class TestSplit {
    public static void main(String[] args) {
        String str = ",12,23,34,45,56,1,,";
        String[] split = str.split(",", -1);
        System.out.println(split.length);
        for (String s: split) {
            System.out.println("==" + s +"==");
        }
    }
}
