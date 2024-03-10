package com.team766.robot.example;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.example.procedures.*;

public class DriverOI extends OIFragment {
    private final JoystickReader leftJoystick;

    // add mechanisms here

    // add any conditions (joystick inputs, etc)
    Condition button0;
    Condition moveJoystick;

    // add any mechanisms to the constructor arguments as well
    public DriverOI(JoystickReader leftJoystick) {
        super("DriverOI");
        this.leftJoystick = leftJoystick;

        button0 = new Condition(() -> leftJoystick.getButton(0));
        moveJoystick = new Condition(() -> leftJoystick.getAxis(0) > 0);
    }

    @Override
    protected void handleOI(Context context) {
        if (button0.isNewlyTriggering()) {
            // handle button press
        } else if (button0.isFinishedTriggering()) {
            // handle button release
        }

        if (moveJoystick.isTriggering()) {
            // handle joystick movement
        }
    }
}
