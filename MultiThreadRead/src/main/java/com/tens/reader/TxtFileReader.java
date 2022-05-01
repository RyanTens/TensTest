package com.tens.reader;

import com.tens.configuration.Configuration;
import com.tens.exchanger.BufferedRawDataExchanger;
import com.tens.util.CounterUtil;
import com.tens.util.ReaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TxtFileReader {
    private static final Logger LOG = LoggerFactory.getLogger(TxtFileReader.class);

    private List<String> sourceFiles;

    private Configuration configuration;

    public TxtFileReader(List<String> sourceFiles, Configuration configuration) {
        this.sourceFiles = sourceFiles;
        this.configuration = configuration;
    }

    public void startRead(String fileName, BufferedRawDataExchanger recordSender, CountDownLatch countDownLatch) {
        LOG.info(String.format("start read [%s]",fileName));
        InputStream inputStream;
        long length = new File(fileName).length();
        try {
            inputStream = new FileInputStream(fileName);
            ReaderUtil.readFromStream(inputStream, fileName, configuration, recordSender);
            recordSender.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOG.error(String.format("找不到待读取文件 : [%s]", fileName));
        }
        CounterUtil.getInstance().addSize(length);
        countDownLatch.countDown();
    }

    public void startRead(BufferedRawDataExchanger recordSender) {
        LOG.info("start read source files...");
        long l = 0L;
        long start = System.currentTimeMillis();
        for (String fileName : this.sourceFiles) {
            LOG.info(String.format("reading file : [%s]", fileName));
            InputStream inputStream;
            long length = new File(fileName).length();
            l += length;
            try {
                inputStream = new FileInputStream(fileName);
                ReaderUtil.readFromStream(inputStream, fileName, configuration, recordSender);
                recordSender.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LOG.error(String.format("找不到待读取文件 : [%s]", fileName));
            }
        }
        long end = System.currentTimeMillis();
        double time = (end - start) / 1000.0;
        double size = l / 1024.0 / 1024;
        double speed = size / time;
        LOG.info(String.format("read speed is [%s]M/s", speed));

        recordSender.terminate();
        LOG.info("end read source files...");
        LOG.info(String.format("read [%s] lines", ReaderUtil.readCount));
    }


}
