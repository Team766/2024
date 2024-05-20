package com.team766.framework.resources;

@FunctionalInterface
public interface ReservingRunnable {
    void run() throws ResourceUnavailableException;
}
