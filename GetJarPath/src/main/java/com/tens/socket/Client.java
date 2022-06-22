package com.tens.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client {
    Socket client;
    BufferedReader in;
    PrintWriter out;


    public Client() {

    }

    public void start() {
        String localhost = "127.0.0.1";
        try {
            //连接master
            client = new Socket(localhost, 10000);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader line = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String str = line.readLine();
                out.println(str);
                out.flush();
                if (str.equals("end")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("error:client:start");
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}