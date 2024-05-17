package com.team766.framework.conditions;

@FunctionalInterface
public interface ReservingRunnable {
    void run() throws ResourceUnavailableException;
}
