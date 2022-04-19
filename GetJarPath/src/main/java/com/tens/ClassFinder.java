package com.tens;

import java.io.UnsupportedEncodingException;

public class ClassFinder {

    public ClassFinder() {
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        ClassFinder classFinder = new ClassFinder();
        String path = classFinder.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        path = java.net.URLDecoder.decode(path,"utf-8");
        System.out.println(path);

    }
}
