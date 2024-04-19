package com.team766.hal.wpilib;

import com.team766.hal.RelayOutput;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;

public class Relay extends edu.wpi.first.wpilibj.Relay implements RelayOutput {
    public Relay(final int channel) {
        super(channel);
    }

    @Override
    public void set(final com.team766.hal.RelayOutput.Value value) {
        edu.wpi.first.wpilibj.Relay.Value wpi_value = null;
        switch (value) {
            case kOff:
                wpi_value = edu.wpi.first.wpilibj.Relay.Value.kOff;
                break;
            case kOn:
                wpi_value = edu.wpi.first.wpilibj.Relay.Value.kOn;
                break;
            case kForward:
                wpi_value = edu.wpi.first.wpilibj.Relay.Value.kForward;
                break;
            case kReverse:
                wpi_value = edu.wpi.first.wpilibj.Relay.Value.kReverse;
                break;
            default:
                LoggerExceptionUtils.logException(
                        new UnsupportedOperationException(
                                "invalid relay output provided. provided value: " + value));
                break;
        }
        if (wpi_value == null) {
            Logger.get(Category.HAL)
                    .logRaw(Severity.ERROR, "Relay value is not translatable: " + value);
            wpi_value = edu.wpi.first.wpilibj.Relay.Value.kOff;
        }
        super.set(wpi_value);
    }
}
