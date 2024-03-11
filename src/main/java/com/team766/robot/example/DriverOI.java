package com.team766.robot.example;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.example.constants.InputConstants;
import com.team766.robot.example.procedures.*;

public class DriverOI extends OIFragment {
    private final JoystickReader joystick;
    private double joystickX;
    private double joystickY;

    // add any conditions (joystick inputs, etc)
    Condition button1;
    Condition moveJoystick;

    // add any mechanisms to the constructor arguments as well
    public DriverOI(JoystickReader joystick) {
        super("DriverOI");
        this.joystick = joystick;

        button1 = new Condition(() -> joystick.getButton(InputConstants.BUTTON_TRIGGER));
        moveJoystick = new Condition(() -> Math.abs(joystickX) > 0 || Math.abs(joystickY) > 0);
    }

    @Override
    protected void handlePre() {
        joystickX = joystick.getAxis(InputConstants.AXIS_X);
        joystickY = joystick.getAxis(InputConstants.AXIS_Y);
    }

    @Override
    protected void handleOI(Context context) {
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
