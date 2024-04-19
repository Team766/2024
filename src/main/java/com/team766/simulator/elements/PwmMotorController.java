package com.team766.simulator.elements;

import com.team766.simulator.ProgramInterface;
import com.team766.simulator.interfaces.ElectricalDevice;

public class PwmMotorController extends MotorController {

    private int channel;

    public PwmMotorController(final int channel_, final ElectricalDevice downstream) {
        super(downstream);

        this.channel = channel_;
    }

    @Override
    protected double getCommand() {
        return ProgramInterface.pwmChannels[channel];
    }
}
