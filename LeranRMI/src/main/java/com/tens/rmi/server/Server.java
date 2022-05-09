package com.tens.rmi.server;

import com.tens.rmi.shared.TensTest;
import com.tens.rmi.shared.WorldClock;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public static void main(String[] args) throws RemoteException {
//        System.out.println("create World clock remote service...");
//        WorldClock worldClock = new WorldClockService();
//        WorldClock skeleton = (WorldClock) UnicastRemoteObject.exportObject(worldClock, 0);
//        Registry registry = LocateRegistry.createRegistry(1999);
//        registry.rebind(WorldClock.class.getSimpleName(), skeleton);

        System.out.println("create tens test remote service");
        TensTestService tensTest = new TensTestService();
        TensTest skeleton = (TensTest) UnicastRemoteObject.exportObject(tensTest, 0);
        Registry registry = LocateRegistry.createRegistry(9393);
        registry.rebind(TensTest.class.getSimpleName(), skeleton);
    }
}
