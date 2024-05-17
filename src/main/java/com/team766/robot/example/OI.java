package com.team766.robot.example;

import com.team766.framework.OIBase;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.robot.example.procedures.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends OIBase {
    private JoystickReader joystick0;
    private JoystickReader joystick1;
    private JoystickReader joystick2;

    public OI() {
        joystick0 = RobotProvider.instance.getJoystick(this, 0);
        joystick1 = RobotProvider.instance.getJoystick(this, 1);
        joystick2 = RobotProvider.instance.getJoystick(this, 2);
    }

    @Override
    protected void dispatch() {
        // Add driver controls here.
    }
}
