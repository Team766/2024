package com.team766.hal.wpilib;

import com.team766.hal.JoystickReader;

public class Joystick extends edu.wpi.first.wpilibj.Joystick implements JoystickReader {
    public Joystick(final int port) {
        super(port);
    }

    @Override
    public double getAxis(final int axis) {
        return getRawAxis(axis);
    }

    @Override
    public boolean getButton(final int button) {
        return getRawButton(button);
    }

    @Override
    public int getPOV() {
        return super.getPOV();
    }
}
