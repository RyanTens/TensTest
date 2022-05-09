package com.tens.rmi.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface TensTest extends Remote {

    String doRead(ArrayList<String> files) throws RemoteException;

    String ping(String ping) throws RemoteException;
}
