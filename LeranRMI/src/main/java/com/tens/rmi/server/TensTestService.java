package com.tens.rmi.server;

import com.tens.rmi.shared.TensTest;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class TensTestService implements TensTest {
    @Override
    public String doRead(ArrayList<String> files) throws RemoteException {
        StringBuffer sb = new StringBuffer();
        for (String file: files) {
            System.out.println(file);
            sb.append(file).append(",");
        }

        return sb.toString();
    }
}
