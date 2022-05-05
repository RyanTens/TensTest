package com.tens.filechannel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileChannelDemo3 {
    public static void main(String[] args) throws IOException {
        RandomAccessFile aFile = new RandomAccessFile("D:\\tmp\\001.txt", "rw");
        FileChannel fromChannel = aFile.getChannel();

        RandomAccessFile bFile = new RandomAccessFile("D:\\tmp\\002.txt", "rw");
        FileChannel toChannel = bFile.getChannel();

        long position = 0L;
        long size = fromChannel.size();
        toChannel.transferFrom(fromChannel, position, size);

        aFile.close();
        bFile.close();
    }
}
