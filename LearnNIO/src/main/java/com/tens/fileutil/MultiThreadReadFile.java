package com.tens.fileutil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

public class MultiThreadReadFile {

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        System.out.println(new Date());
        File InputFile = new File("D:\\tmp\\bigfile\\test_10m.txt");
        int threads = 10;
        String Prefix = "part_";
        ArrayList<Section> Sect = new ArrayList<>();
        Sect.add(new Section(0));
        long size = InputFile.length() / threads;
        RandomAccessFile in_file = new RandomAccessFile(InputFile, "r");
        while (true) {
            Section se = Sect.get(Sect.size() - 1);
            if (se.Start + size > InputFile.length()) {
                se.Size = InputFile.length() - se.Start;
                break;
            }
            se.Size = size;
            in_file.seek(se.Start + size);
            int b = in_file.read();
            while (b != '\n' && b != '\r' && b >= 0) {
                se.Size++;
                b = in_file.read();
            }
            se.Size++;
            b = in_file.read();
            while (b == '\n' || b == '\r') {
                se.Size++;
                b = in_file.read();
            }
            if (se.Start + se.Size < InputFile.length()) {
                Sect.add(new Section(se.Start + se.Size));
            } else {
                break;
            }
        }
        in_file.close();

        Thread[] t = new Thread[threads];
        for (int i = 0; i < t.length; i++) {
            int finalI = i;
            t[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        char[] buffer = new char[1024 * 1024];
//                        RandomAccessFile reader = new RandomAccessFile(InputFile, "r");
                        BufferedReader reader = new BufferedReader(new FileReader(InputFile));
                        BufferedWriter writer = new BufferedWriter(new FileWriter(Prefix + "split" + finalI + ".txt"), 1024 * 1024);
                        String line;
                        reader.skip(Sect.get(finalI).Start);
                        long Size = Sect.get(finalI).Size;
                        System.out.println(new Date() + "\tThread" + finalI + ": write Start");
                        if (Size - buffer.length <= 0) {
                            buffer = new char[(int) Size];
                        }

                        long readSize = 0L;
                        while ((line = reader.readLine()) != null && Size > 0) {
                            Size -= line.getBytes(StandardCharsets.UTF_8).length;
                            String[] split = line.split(",");
                            if (split[0].length() != "20210101111013".length()) {
                                System.out.println(line);
                            }
                        }
//                        while (reader.read(buffer) != -1 && Size > 0) {
//                            writer.write(buffer);
//                            Size -= buffer.length;
//                            if (Size - buffer.length <= 0) {
//                                buffer = new char[(int) Size];
//                            }
//                        }
//                        if (Size > 0) {
//                            for (int j = 0; j < buffer.length && Size > 0; j++) {
//                                writer.write(buffer[j]);
//                                Size--;
//                            }
//                        }
                        System.out.println(new Date() + "\tThread" + finalI + ": write finish");
                        reader.close();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t[i].start();
        }
        for (int i = 0; i < t.length; i++) {
            t[i].join();
        }
        System.out.println(new Date());
    }
}

/**
 * Section
 */
class Section {
    public long Start;
    public long Size;

    public Section(long start) {
        this.Start = start;
    }
}
