package com.team766.orin;

public class NoTagFoundError extends Exception {
    public NoTagFoundError(int id) {
        super("No tag found on table with given id of: " + id);
    }
}
