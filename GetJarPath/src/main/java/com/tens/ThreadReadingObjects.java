package com.tens;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


class ThreadReadingObjects {

    static final CellProcessor[] userProcessors = new CellProcessor[] {
            new Unique(new ParseInt()),//唯一的，int id
            new Unique(new StrMinMax(5, 20)),//唯一的，长度为5到20
            new StrMinMax(8, 35), //长度是8到35
            new ParseDate("dd/MM/yyyy"), //格式为天/月/年（day/month/year）
            new Optional(new ParseInt()), //整型数字，但只有这列有值的时候ParseInt处理器才会去处理这个值（其实就是该列可以为空）
            null //不使用处理器
    };

    public static void main(String[] args) throws Exception {
        // InputStreamReader freader = new InputStreamReader(inputStream,"UTF-8");
        // ICsvBeanReader inFile = new CsvBeanReader(freader, CsvPreference.STANDARD_PREFERENCE);

        ICsvBeanReader inFile = new CsvBeanReader(new FileReader("D:\\foo.csv"), CsvPreference.STANDARD_PREFERENCE);

        ExecutorService executorService = null;
        try {
            //如果你的CSV文件没有头，你也可以定义个数组来替代：
            // final String[] header = new String[] { "id","username", "password", "date", "zip", "town"};
            final String[] header = inFile.getHeader(true);

            //创建线程池
            //注意： 线程数不宜过多,jdbc操作时会占用连接数,过多会超出数据库连接
            List<Future<String>> futureList = new ArrayList<Future<String>>();
            executorService = Executors.newFixedThreadPool(5);

            //分页读取数据后,加入线程池处理
            while (getPageUserList(executorService,futureList,inFile, header)) {}

            //获取线程处理结果
            for (Future<String> future : futureList) {
                while (true) {
                    if (future.isDone() && !future.isCancelled()) {
                        System.out.println("future result: "+future.get());
                        break;
                    } else {
                        Thread.sleep(1000);
                    }
                }
            }

        } finally {
            inFile.close();
            executorService.shutdown();
        }
    }

    private static boolean getPageUserList(ExecutorService executorService, List<Future<String>> futureList, ICsvBeanReader inFile, String[] header) throws IOException {
        int index = 0;
        boolean status = false;
        List<UserBean> userBeans = new ArrayList<UserBean>();
        UserBean user;
        while ((user = inFile.read(UserBean.class, header, userProcessors)) != null) {// 这里从第一行开始取数据
            userBeans.add(user);
            index++;
            //每次读取的行数，每个线程处理的记录数,根据实际情况修改
            if (index == 10) {
                status = true;
                break;
            }
        }
        //添加到线程集合
        if(!userBeans.isEmpty()){
            Future<String> future = executorService.submit(getUpdateDbJob(futureList.size(),userBeans));
            futureList.add(future);
        }

        return status;
    }

    private static Callable<String> getUpdateDbJob(int threadNo,List<UserBean> userBeans) {
        return new Callable<String>() {
            @Override
            public String call() throws Exception {
                int count = userBeans.size();
                //第一种： 数组List函数分批量处理方法
                batchPageInsertDataOne(threadNo,userBeans);

                //第二种：取% 分批处理方法
//                batchPageInsertDataTwo(threadNo,userBeans);

                return String.valueOf(count);
            }
        };
    }

    private static void batchPageInsertDataOne(int threadNo,List<UserBean> userBeans){
        int perCount = 4, index = 0;
        int times = userBeans.size() / perCount;
        long stime=System.currentTimeMillis();

        try {
            do {
                // 休眠50ms
                Thread.sleep(50);

                List<UserBean> listTemp= null;

                if (userBeans.size() >= perCount) {
                    listTemp = userBeans.subList(0, perCount);// listTemp是分段处理逻辑的参数
                    System.out.println("线程"+threadNo+"更新用户："+listTemp.size()+" 个");
                }else{
                    listTemp = userBeans.subList(0, userBeans.size());// listTemp是分段处理逻辑的参数
                    System.out.println("线程"+threadNo+"更新用户："+listTemp.size()+" 个");
                }

                // 事务单元执行个数==尽量在事务里面处理少一点(事务尽量小一点)
                //注意: 每次分批事务提交时数量不宜过多,太多会造成行锁；
                jdbcPerBatchInsert(listTemp);

                userBeans.removeAll(listTemp);

                index++;

            }while(index<= times);

            // 计算时间
            long etime=System.currentTimeMillis();
            System.out.println("线程"+threadNo+"批量事务插入总共耗时-----------------------:"+(etime-stime)+"ms!");

        }catch(Exception e) {
            e.printStackTrace();

            System.out.println("JDBC批量执行插入异常:>>" + userBeans.size());

            throw new RuntimeException();

        }
    }

    private static void batchPageInsertDataTwo(int threadNo,List<UserBean> userBeans){
        long stime=System.currentTimeMillis();

        try {
            //分批量写入数据库
            int perCount = 4;
            List<UserBean> userList = new ArrayList<UserBean>();
            for(int i=0;i<userBeans.size();i++){
                userList.add(userBeans.get(i));
                //如果数据量比较大再次事务分批commit,提交 perCount 条记录
                //取 % 条数根据实际情况修改
                if (i > 0 && i % perCount == 0) {
                    System.out.println("线程"+threadNo+"更新用户："+userList.size()+" 个成功");
                    //采用jdbcTemplate 批量写入数据库
                    jdbcPerBatchInsert(userBeans);

                    userList.clear();
                } else if (i == userBeans.size() - 1) {
                    //处理最后一批数据提交
                    System.out.println("线程"+threadNo+"更新用户："+userList.size()+" 个成功");

                    //采用jdbcTemplate 批量写入数据库
                    jdbcPerBatchInsert(userBeans);
                    userList.clear();
                }
            }

            // 计算时间
            long etime=System.currentTimeMillis();
            System.out.println("线程"+threadNo+"批量事务插入总共耗时-----------------------:"+(etime-stime)+"ms!");
        }catch(Exception e) {
            e.printStackTrace();

            System.out.println("JDBC批量执行插入异常:>>" + userBeans.size());

            throw new RuntimeException();

        }

    }
    /**
     * 采用jdbcTemplate 批量写入数据库
     * @param listTemp
     */
    private static void jdbcPerBatchInsert(List<UserBean> listTemp){

    }
}

