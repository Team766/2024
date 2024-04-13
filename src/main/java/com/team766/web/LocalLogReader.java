package com.team766.web;

public class LocalLogReader {
    public static void main(String[] args) {
        var webServer = new WebServer();
        webServer.addHandler(new ReadLogs());
        webServer.start();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
}
