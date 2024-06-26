package com.team766.hal.mock;

import com.team766.hal.RelayOutput;

public class MockRelay implements RelayOutput {

    private Value val;

    public MockRelay(final int port) {
        val = Value.kOff;
    }

    @Override
    public void set(final Value out) {
        val = out;
    }

    public Value get() {
        return val;
    }
}
