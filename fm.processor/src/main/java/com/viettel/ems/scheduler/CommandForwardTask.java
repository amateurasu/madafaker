package com.viettel.ems.scheduler;

import lombok.Data;

import java.util.Date;

@Data
public class CommandForwardTask implements Runnable {

    private final String message;

    @Override
    public void run() {
        System.out.println(new Date() + ": " + message + " on thread " + Thread.currentThread().getName());
    }
}
