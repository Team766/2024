package com.team766.hal.simulator;

import com.team766.hal.BasicMotorController;
import com.team766.hal.ControlInputReader;
import com.team766.hal.LocalMotorController;
import com.team766.simulator.ProgramInterface;

public class SimMotorController extends LocalMotorController {
    public SimMotorController(final String configPrefix, final int address) {
        this(configPrefix, ProgramInterface.canMotorControllerChannels[address]);
    }

    SimMotorController(
            final String configPrefix,
            final ProgramInterface.CANMotorControllerCommunication channel) {
        super(
                configPrefix,
                new SimBasicMotorController(channel),
                new ControlInputReader() {
                    @Override
                    public double getPosition() {
                        return channel.status.sensorPosition;
                    }

                    @Override
                    public double getRate() {
                        return channel.status.sensorVelocity;
                    }
                });
    }
}

class SimBasicMotorController implements BasicMotorController {
    private final ProgramInterface.CANMotorControllerCommunication channel;

    SimBasicMotorController(final int address) {
        this(ProgramInterface.canMotorControllerChannels[address]);
    }

    SimBasicMotorController(final ProgramInterface.CANMotorControllerCommunication channel_) {
        this.channel = channel_;
    }

    @Override
    public double get() {
        return channel.command.output;
    }

    @Override
    public void set(double power) {
        power = Math.min(Math.max(-1, power), 1);
        channel.command.output = power;
        channel.command.controlMode =
                ProgramInterface.CANMotorControllerCommand.ControlMode.PercentOutput;
    }

    @Override
    public void restoreFactoryDefault() {}
}
