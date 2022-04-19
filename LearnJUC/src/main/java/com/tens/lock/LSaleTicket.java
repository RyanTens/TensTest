package com.tens.lock;


public class LSaleTicket {
    public static void main(String[] args) {
        LTicket ticket = new LTicket();

        new Thread(() -> {
                for (int i = 0; i < 40; i++) {
                    ticket.sale();
                }

        }, "lsaler1").start();

        new Thread(() -> {

                for (int i = 0; i < 40; i++) {
                    ticket.sale();
                }

        }, "lsaler2").start();

        new Thread(() -> {

                for (int i = 0; i < 40; i++) {
                    ticket.sale();
                }
        }, "lsaler3").start();
    }
}
