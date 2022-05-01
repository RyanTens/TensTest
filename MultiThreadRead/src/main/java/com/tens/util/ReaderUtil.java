package com.tens.util;

import com.alibaba.fastjson.JSON;
import com.csvreader.CsvReader;
import com.tens.configuration.Configuration;
import com.tens.exchanger.BufferedRawDataExchanger;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ReaderUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReaderUtil.class);

    public static long readCount = 0L;

    public static void readFromStream(InputStream inputStream,
                                      String fileName,
                                      Configuration configuration,
                                      BufferedRawDataExchanger recordSender) {
        String encoding = configuration.getEncoding();

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(inputStream,
                    encoding), 8 * 1024);
            ReaderUtil.doReadFromStream(reader, fileName, configuration, recordSender);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }


    }

    private static void doReadFromStream(BufferedReader reader,
                                         String fileName,
                                         Configuration configuration,
                                         BufferedRawDataExchanger recordSender) {
        Character fieldDelimiter = configuration.getFieldDelimiter();
        boolean skipHead = configuration.isSkipHead();
        CsvReader csvReader = null;

        try {
            if (skipHead) {
                String fetchLine = reader.readLine();
                LOG.info(String.format("Head line [%s] has been skipped", fetchLine));
            }
            csvReader = new CsvReader(reader);
            csvReader.setDelimiter(fieldDelimiter);
            csvReader.setSafetySwitch(false);

            String[] parseRows;
            while ((parseRows = ReaderUtil.splitBufferedReader(csvReader)) != null) {
                readCount++;
                ReaderUtil.transportOneRecord(recordSender, parseRows);
            }
            LOG.info(String.format("CsvReader使用默认值[%s]", JSON.toJSONString(csvReader)));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void transportOneRecord(BufferedRawDataExchanger recordSender, String[] parseRows) {
        recordSender.sendToChannel(parseRows);
    }

    private static String[] splitBufferedReader(CsvReader csvReader) throws IOException {
        String[] splitResult = null;
        if (csvReader.readRecord()) {
            splitResult = csvReader.getValues();
        }
        return splitResult;
    }
}
