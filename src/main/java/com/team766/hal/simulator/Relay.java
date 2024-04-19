package com.team766.hal.simulator;

import com.team766.hal.RelayOutput;
import com.team766.simulator.ProgramInterface;

public class Relay implements RelayOutput {

    private int channel;

    public Relay(final int channel_) {
        this.channel = channel_;
    }

    @Override
    public void set(final Value out) {
        switch (out) {
            case kForward:
                ProgramInterface.relayChannels[channel] = 1;
                break;
            case kOff:
                ProgramInterface.relayChannels[channel] = 0;
                break;
            case kOn:
                ProgramInterface.relayChannels[channel] = 1;
                break;
            case kReverse:
                ProgramInterface.relayChannels[channel] = -1;
                break;
            default:
                break;
        }
    }
}
