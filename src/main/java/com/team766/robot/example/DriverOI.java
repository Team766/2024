package com.team766.robot.example;

import com.team766.framework.Condition;
import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.example.constants.InputConstants;
import com.team766.robot.example.procedures.*;

public class DriverOI extends OIFragment {
    private final JoystickReader joystick;

    // add any conditions (joystick inputs, etc)
    private final Condition button1;
    private final InlineCondition moveJoystick = new InlineCondition();

    // add any mechanisms to the constructor arguments as well
    public DriverOI(JoystickReader joystick) {
        this.joystick = joystick;

        button1 = joystick.getButtonCondition(InputConstants.BUTTON_TRIGGER);
    }

    @Override
    protected void handleOI(Context context) {
        final double joystickX = joystick.getAxis(InputConstants.AXIS_X);
        final double joystickY = joystick.getAxis(InputConstants.AXIS_Y);

        moveJoystick.update(Math.abs(joystickX) > 0 || Math.abs(joystickY) > 0);

        if (button1.isNewlyTriggering()) {
            // handle button press
        } else if (button1.isFinishedTriggering()) {
            // handle button release
        }

        if (moveJoystick.isTriggering()) {
            // handle joystick movement
        }
    }
}
