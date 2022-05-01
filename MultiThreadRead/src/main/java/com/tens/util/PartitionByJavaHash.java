package com.tens.util;

public class PartitionByJavaHash {
    /**
     * 根据键值Hash计算出数据所属的分区
     * @param columnValue 键值
     * @param partitionSize 分区大小
     * @return Integer 分区号
     */
    public static Integer calculate(final String columnValue, final int partitionSize) {

        return hash(columnValue) % partitionSize;

    }

    public static Integer calculate(final String columnValue) {
        return 0;
    }

    private static int hash(Object key) {
        return key == null ? 0 : key.hashCode() & 0x7FFFFFFF;
    }
}
