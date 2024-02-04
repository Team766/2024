package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
    private static final double POWER_DAMPEN_FACTOR = 0.15;

    private JoystickReader joystick0;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        joystick0 = RobotProvider.instance.getJoystick(0);
    }

    public void run(final Context context) {
        context.takeOwnership(Robot.drive);
        while (true) {
            // wait for driver station data (and refresh it using the WPILib APIs)
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();
            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.
            Robot.drive.drive(
                    POWER_DAMPEN_FACTOR * joystick0.getAxis(0),
                    POWER_DAMPEN_FACTOR * joystick0.getAxis(0));
        }
    }
}
