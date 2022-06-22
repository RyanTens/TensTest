package com.tens.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MasterSlaver {
    //master的IP 与 监听port
    private String masterAddr = "127.0.0.1";
    private int masterRegitePort = 10001;

    //slaver接受任务的服务端口
    private ServerSocket server;
    private int serverPort = 8081;
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public MasterSlaver() {

    }

    //连接master,注册
    public void register() {
        try {
            //连接master,并向其发送自己的服务端口号
            Socket registSocket = new Socket(masterAddr, masterRegitePort);
            String reportMsg = "i am slaver：" + serverPort;


            //与master的注册连接发送流，发送自己的服务端口号
            out = new PrintWriter(registSocket.getOutputStream(), true);
            out.println(reportMsg);
            out.flush();
            registSocket.close();

            System.out.println("我是slaver " + serverPort + "已经向master" + masterAddr + ":" + masterRegitePort + "注册");
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //启动监听服务端口，接收master调用，完成打印任务
    public void start() {
        try {
            server = new ServerSocket(serverPort);
            while (true) {
                client = server.accept();
                //接受master调用，开始执行打印任务
                System.out.println("开始执行打印任务：");

                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);

                String line = in.readLine();
                while (line != null) {
                    System.out.println("getTask : from master:" + line);
                    line = in.readLine();
                }
                System.out.println("任务执行完毕，服务端口为:" + serverPort);
                out.println("任务执行完毕,服务端口为:" + serverPort + "---下次任务再见！---");
                out.close();
                client.close();
            }
        } catch (IOException e) {
            System.out.println("error:masterSlaver " + serverPort + " in method start");
        }
    }


    public static void main(String[] args) {
        MasterSlaver slaver = new MasterSlaver();
        slaver.register();
        slaver.start();
    }
}

