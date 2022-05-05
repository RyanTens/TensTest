package com.tens.filechannel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class FileChannelDemo2 {
    public static void main(String[] args) throws IOException {
        //打开fileChannel
        RandomAccessFile aFile = new RandomAccessFile("D:\\tmp\\001.txt", "rw");
        FileChannel channel = aFile.getChannel();

        //创建buffer对象
        ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);

        String newData = "tenssssss";
        buffer.clear();

        buffer.put(newData.getBytes(StandardCharsets.UTF_8));
        buffer.flip();

        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        channel.close();
    }
}
