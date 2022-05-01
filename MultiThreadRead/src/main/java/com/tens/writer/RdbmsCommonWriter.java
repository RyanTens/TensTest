package com.tens.writer;

import com.tens.configuration.Configuration;
import com.tens.exchanger.BufferedHashedRecordExchanger;
import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdbmsCommonWriter {

    private static final Logger LOG = LoggerFactory.getLogger(RdbmsCommonWriter.class);

    private Configuration configuration;

    public RdbmsCommonWriter(Configuration configuration) {
        this.configuration = configuration;
    }

    public void startWrite(BufferedHashedRecordExchanger reciever) {
        LOG.info("start write...");
        String[] record;
        long count = 0L;
        while ((record = reciever.getFromChannel()) != null && record.length != 0) {
            count++;
            StrBuilder sb = new StrBuilder();
            for (String s: record) {
                sb.append(s).append(",");
            }
            if (count % 1000000 == 0) {
                LOG.info(sb.toString());
            }
        }
        LOG.info(String.format("write [%s] lines records!", count));
    }
}
