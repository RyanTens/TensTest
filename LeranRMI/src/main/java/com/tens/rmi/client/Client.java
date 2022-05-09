package com.tens.rmi.client;

import com.tens.rmi.shared.TensTest;
import com.tens.rmi.shared.WorldClock;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Client {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry("localhost", 1999);
//        WorldClock worldClock = (WorldClock) registry.lookup(WorldClock.class.getSimpleName());
//        LocalDateTime now = worldClock.getLocalDateTime("Asia/Shanghai");
//        System.out.println(now);
        TensTest tensTest = (TensTest) registry.lookup(TensTest.class.getSimpleName());
        ArrayList<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        String s = tensTest.doRead(list);
        System.out.println(s);
    }
}
