package com.tens.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


//master负责两项工作，1，注册；2，分发任务。
//1，注册包括接收slaver的注册，将slaver的服务端口保存到list表中
//2，分发任务包括接收client发来的数据，交给list表中任意一个salver执行
public class Master {
    //master接收client数据的端口
    private int clientPort = 10000;
    //master接收salver注册的端口
    private int slaverPort = 10001;
    //salver的注册list
    private static ArrayList<Integer> slaverMap;

    public Master() {
        slaverMap = new ArrayList<Integer>();
    }

    public void start() {
//启动侦听slaver线程
        Thread tSlaver = new listenSlaver();
        tSlaver.start();

//启动侦听client线程
        Thread tClient = new listenClient();
        tClient.start();
    }

    //侦听client
    class listenClient extends Thread {
        private ServerSocket server2Client;
        private Socket client;
        private BufferedReader in;

        public listenClient() {
            try {
                server2Client = new ServerSocket(clientPort);
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void run() {
            System.out.println("启动了一个监听client的线程");
            while (true) {
                try {
                    client = server2Client.accept();
                    System.out.println("监听到一个client");
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));

//获取client需要打印值
                    String line = in.readLine();
                    while (line != null) {
                        System.out.println("Master:from client:" + client.getInetAddress() + ",客户端输入:" + line);

//找到空闲slaver，命令其打印
                        int slaverPort = slaverMap.get((int) (Math.random() * slaverMap.size()));

                        System.out.println("任务交给" + slaverPort);
                        System.out.println("任务:" + line);
                        Socket scoket2Slaver = new Socket("127.0.0.1", slaverPort);

                        PrintWriter out2Slaver = new PrintWriter(scoket2Slaver.getOutputStream(), true);
                        ;

                        out2Slaver.println("任务:" + line);
                        out2Slaver.flush();
                        scoket2Slaver.close();

                        line = in.readLine();

                        System.out.println("一次任务执行完毕，等待……");
                    }
                    client.close();
                } catch (IOException e) {
                    System.out.println("error:Master:start");
                }
            }
        }
    }

    //侦听slaver
    class listenSlaver extends Thread {
        private ServerSocket server2Slaver;
        private Socket slaver;
        private BufferedReader in;

        public listenSlaver() {
            try {
                server2Slaver = new ServerSocket(slaverPort);
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void run() {
            System.out.println("启动了一个监听slaver的线程");
            try {
                while (true) {
                    slaver = server2Slaver.accept();
                    System.out.println("有一个slaver注册成功，slaver port:" + slaver.getPort());


                    in = new BufferedReader(new InputStreamReader(slaver.getInputStream()));


                    String line = in.readLine();


                    while (line != null) {
                        //获取slaver的port值，注册
                        slaverMap.add(Integer.parseInt(line.substring(12)));
                        System.out.println("Master:from slaver:" + slaver.getInetAddress() + ":" + slaver.getPort() + ",其服务端口:" + line.substring(12));
                        line = in.readLine();
                    }
                    slaver.close();
                }
            } catch (IOException e) {
                System.out.println("error:Master:listenSlaver");
            }
        }
    }
}
