package com.tens.channel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileChannelDemo1 {
    public static void main(String[] args) throws IOException {
        //创建FileChannel
        RandomAccessFile aFile = new RandomAccessFile("D:\\tmp\\g2021-01-01_b9025cb9458e41bf954772071dc490c7", "rw");
        FileChannel channel = aFile.getChannel();

        //创建Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

        //读取数据到Buffer
        int bytesRead = channel.read(buffer);

        ByteBuffer stringBuffer = ByteBuffer.allocate(20);

        while (bytesRead != -1) {
            System.out.println("读取了：" + bytesRead);
            buffer.flip();
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                if (b == 10 || b == 13) {
                    stringBuffer.flip();
                    final String line = Charset.forName("utf-8").decode(stringBuffer).toString();
                    System.out.println(line);
                } else {
                    if (stringBuffer.hasRemaining()) {
                        stringBuffer.put(b);
                    } else {
                        stringBuffer = reAllocate(stringBuffer);
                        stringBuffer.put(b);
                    }
                }
            }
            buffer.clear();
            bytesRead = channel.read(buffer);
        }
        aFile.close();
        System.out.println("结束了");
    }

    private static ByteBuffer reAllocate(ByteBuffer stringBuffer) {
        final int capacity = stringBuffer.capacity();
        byte[] newBuffer = new byte[capacity * 2];
        System.arraycopy(stringBuffer.array(), 0, newBuffer, 0, capacity);
        return (ByteBuffer) ByteBuffer.wrap(newBuffer).position(capacity);
    }
}
