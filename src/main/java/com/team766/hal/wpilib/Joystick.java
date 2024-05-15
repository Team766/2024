package com.team766.hal.wpilib;

import com.team766.framework.conditions.Condition;
import com.team766.framework.conditions.RulesMixin;
import com.team766.hal.JoystickReader;
import com.team766.library.ArrayUtils;
import com.team766.library.Lazy;

public class Joystick extends edu.wpi.first.wpilibj.Joystick implements JoystickReader {
    private final Lazy<Condition>[] buttonConditions;
    private final Condition fallbackCondition;

    public Joystick(RulesMixin oi, final int port) {
        super(port);
        buttonConditions = ArrayUtils.initializeArray(
                getButtonCount(),
                button -> new Lazy<>(
                        () -> oi.new DeclaredCondition(() -> this.getButtonState(button))));
        fallbackCondition = oi.neverCondition;
    }

    @Override
    public double getAxis(final int axis) {
        return getRawAxis(axis);
    }

    @Override
    public boolean getButtonState(final int button) {
        return getRawButton(button);
    }

    @Override
    public Condition getButton(int button) {
        if (button < buttonConditions.length) {
            return buttonConditions[button].get();
        } else {
            return fallbackCondition;
        }
    }
}
