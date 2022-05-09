package com.tens.rmi.server;

import com.tens.rmi.shared.WorldClock;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class WorldClockService implements WorldClock {
    @Override
    public LocalDateTime getLocalDateTime(String zoneId) throws RemoteException {
        System.out.println("get local date time...");
        return LocalDateTime.now(ZoneId.of(zoneId)).withNano(0);
    }
}
