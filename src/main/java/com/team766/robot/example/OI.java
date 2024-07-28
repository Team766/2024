package com.team766.robot.example;

import static com.team766.framework.resources.Guarded.guard;

import com.team766.framework.OIBase;
import com.team766.framework.resources.Guarded;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.robot.example.mechanisms.*;
import com.team766.robot.example.procedures.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends OIBase {
    private JoystickReader joystick0;
    private JoystickReader joystick1;
    private JoystickReader joystick2;
    private Guarded<ExampleMechanism> exampleMechanism;

    public OI(ExampleMechanism exampleMechanism) {
        joystick0 = RobotProvider.instance.getJoystick(0);
        joystick1 = RobotProvider.instance.getJoystick(1);
        joystick2 = RobotProvider.instance.getJoystick(2);
        this.exampleMechanism = guard(exampleMechanism);
    }

    @Override
    protected void dispatch() {
        // Add driver controls here.
    }
}
