package com.team766.hal.simulator;

import com.team766.hal.DigitalInputReader;
import com.team766.simulator.ProgramInterface;

public class DigitalInput implements DigitalInputReader {

    private final int channel;

    public DigitalInput(final int channel_) {
        this.channel = channel_;
    }

    public boolean get() {
        return ProgramInterface.digitalChannels[channel];
    }
}
