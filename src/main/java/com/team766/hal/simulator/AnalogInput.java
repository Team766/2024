package com.team766.hal.simulator;

import com.team766.hal.AnalogInputReader;
import com.team766.simulator.ProgramInterface;

public class AnalogInput implements AnalogInputReader {

    private final int channel;

    public AnalogInput(final int channel_) {
        this.channel = channel_;
    }

    @Override
    public double getVoltage() {
        return ProgramInterface.analogChannels[channel];
    }
}
