package com.tens.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class TestWrite2Local {
    private static final Logger LOG = Logger.getLogger("TestWrite2Local");
    public static void main(String[] args) {
        LOG.info("start...");
        String path = "./localfile/";
        String endFileStr = path + "test.txt";
        File file = new File(path);
        String line = "test file writer...\n";
        FileWriter fw = null;
        BufferedWriter bw = null;
        File endFile = new File(endFileStr);

        try {
            if (!file.exists()) {
                file.mkdir();
            }

            file.createNewFile();
            fw = new FileWriter(endFile, true);
            bw = new BufferedWriter(fw);
            bw.write(line);
            bw.flush();
            File desFile = new File("./finalpath/final.txt");
            boolean b = file.renameTo(desFile);
            LOG.info(b +"");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void deleteFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                LOG.info(String.format("delete file,file path is [%s]", files[i].toString()));
                files[i].delete();
            }
        }
    }
}
