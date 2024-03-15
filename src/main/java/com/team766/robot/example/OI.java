package com.team766.robot.example;

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
    // input devices
    private final JoystickReader joystick;
    private final JoystickReader gamepad;

    // OIFragments (driver, boxop, etc)
    private final DriverOI driverOI;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        joystick = RobotProvider.instance.getJoystick(0);
        gamepad = RobotProvider.instance.getJoystick(1);
        driverOI = new DriverOI(joystick);
    }

    public void run(final Context context) {
        while (true) {
            // wait for driver station data (and refresh it using the WPILib APIs)
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            // call each of the OI fragment's run methods.
            driverOI.run(context);
        }
    }
}
