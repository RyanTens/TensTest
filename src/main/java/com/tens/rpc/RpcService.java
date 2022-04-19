package com.tens.rpc;

import java.net.Socket;
import java.util.Map;

public class RpcService implements Runnable{
    private Socket client;
    private Map<String, Object> services;

    public RpcService(Socket client, Map<String, Object> services) {
        super();
        this.client = client;
        this.services = services;
    }

    @Override
    public void run() {

    }
}
