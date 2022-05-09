package com.tens.container;

import com.tens.channel.HashedRecordChannel;
import com.tens.channel.RawDataChannel;
import com.tens.channel.RecordChannelGroup;
import com.tens.configuration.Configuration;
import com.tens.exchanger.BufferedHashedRecordExchanger;
import com.tens.exchanger.BufferedRawDataExchanger;
import com.tens.reader.TxtFileReader;
import com.tens.transformer.RawDataTransformer;
import com.tens.util.CounterUtil;
import com.tens.writer.RdbmsCommonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SingleThreadContainer {
    private static final Logger LOG = LoggerFactory.getLogger(SingleThreadContainer.class);

    private String dir;

    private ArrayList<String> fileList = new ArrayList<>();

    private Configuration configuration;

    private RawDataChannel rawDataChannel;

    private RecordChannelGroup channelGroup;

    private int channelNum;

    private int transformerNum = 5;

    private ExecutorService transformExecutors;

    private ExecutorService writerExecutors;

    private ArrayList<BufferedHashedRecordExchanger> writerRecievers = new ArrayList<>();
    private ArrayList<BufferedRawDataExchanger> transformerRecievers = new ArrayList<>();
    private BufferedRawDataExchanger readerSender;

    public SingleThreadContainer(Configuration configuration) {
        this.configuration = configuration;
    }

    public void prepare() {
        this.channelNum = configuration.getNodeList().size();
        this.dir = configuration.getFilePath();
    }

    public void init() {
        this.rawDataChannel = new RawDataChannel(configuration);
        File file = new File(dir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f: files) {
                if (f.isFile()) {
                    fileList.add(f.getAbsolutePath());
                }
            }
        } else {
            fileList.add(file.getAbsolutePath());
        }

        channelGroup = new RecordChannelGroup(configuration, channelNum);

        List<HashedRecordChannel> channelList = channelGroup.getChannelList();
        List<HashedRecordChannel> mirrorChannelList = channelGroup.getMirrorChannelList();
        for (int i = 0; i < channelList.size(); i++) {
            writerRecievers.add(new BufferedHashedRecordExchanger(channelList.get(i), configuration));
            writerRecievers.add(new BufferedHashedRecordExchanger(mirrorChannelList.get(i), configuration));
        }

        for (int i = 0; i < transformerNum; i++) {
            transformerRecievers.add(new BufferedRawDataExchanger(rawDataChannel,configuration));
        }

        readerSender = new BufferedRawDataExchanger(rawDataChannel, configuration);

        transformExecutors = Executors.newFixedThreadPool(transformerNum);
        writerExecutors = Executors.newFixedThreadPool(channelNum * 2);

    }

    public void start() throws InterruptedException {
        //先从最底层消费者开始启动
        for (BufferedHashedRecordExchanger reviever : writerRecievers) {
            writerExecutors.execute(() -> {
                new RdbmsCommonWriter(configuration).startWrite(reviever);
            });
        }

        CountDownLatch countDownLatch = new CountDownLatch(transformerRecievers.size());

        for (BufferedRawDataExchanger tr : transformerRecievers) {
            transformExecutors.execute(() -> {
                new RawDataTransformer(configuration, channelGroup, countDownLatch).startTransform(tr);
            });
        }

        ExecutorService readerExecutors = Executors.newFixedThreadPool(5);

        TxtFileReader txtFileReader = new TxtFileReader(fileList, configuration);
        txtFileReader.startRead(readerSender);
//        CountDownLatch fileCountDownLatch = new CountDownLatch(fileList.size());
//        long start = System.currentTimeMillis();
//        for (String f:fileList) {
//            readerExecutors.execute(() -> {
//                TxtFileReader txtFileReader = new TxtFileReader(fileList, configuration);
//                txtFileReader.startRead(f, new BufferedRawDataExchanger(rawDataChannel, configuration), fileCountDownLatch);
//            });
//        }


        transformExecutors.shutdown();
        writerExecutors.shutdown();
        readerExecutors.shutdown();

//        fileCountDownLatch.await();
//        long end = System.currentTimeMillis();
//        CounterUtil.getInstance().addTime(end - start);
        rawDataChannel.pushTerminate();

        countDownLatch.await();
        channelGroup.terminate();


        boolean isFinish = false;

        while (!isFinish) {
            if (transformExecutors.isTerminated() && writerExecutors.isTerminated()) {
                isFinish = true;
//                long size = CounterUtil.getInstance().getSize().get();
//                long time = CounterUtil.getInstance().getTime().get();
//                double speed = size / 1024 / 1024 / (time / 1000.0);
//                LOG.info(String.format("read speed is [%s]M/s", speed));
                LOG.info("jobs done!!");
            }
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public void finish() {

    }
}
