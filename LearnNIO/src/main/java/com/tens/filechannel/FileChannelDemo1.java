package com.tens.filechannel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelDemo1 {
    public static void main(String[] args) throws IOException {
        RandomAccessFile aFile = new RandomAccessFile("D:\\tmp\\g2021-01-01_559baa60deca485faead903982680cac", "r");
        FileChannel channel = aFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);

        int byteRead = channel.read(buffer);
     
    }
}
