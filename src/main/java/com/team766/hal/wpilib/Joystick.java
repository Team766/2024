package com.team766.hal.wpilib;

import com.team766.framework.conditions.RulesMixin;
import com.team766.hal.JoystickReader;
import edu.wpi.first.hal.DriverStationJNI;

public class Joystick extends edu.wpi.first.wpilibj.Joystick implements JoystickReader {
    private final JoystickReader.Companion companion;

    public Joystick(RulesMixin oi, final int port) {
        super(port);
        companion = new JoystickReader.Companion(this, oi);
    }

    @Override
    public Companion getCompanion() {
        return companion;
    }

    @Override
    public double getAxis(final int axis) {
        return getRawAxis(axis);
    }

    @Override
    public int getMaxAxisCount() {
        return DriverStationJNI.kMaxJoystickAxes;
    }

    @Override
    public boolean getButton(final int button) {
        return getRawButton(button);
    }

    @Override
    public int getMaxButtonCount() {
        return 32; // Buttons are represented in the DriverStation as a 32-bit integer.
    }

    @Override
    public int getPOV() {
        return super.getPOV();
    }
}
