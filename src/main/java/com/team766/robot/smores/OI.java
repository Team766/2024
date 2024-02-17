package com.team766.robot.smores;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.smores.procedures.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
    private JoystickReader joystick0;
    private JoystickReader joystick1;
    private JoystickReader macropad;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        joystick0 = RobotProvider.instance.getJoystick(0);
        joystick1 = RobotProvider.instance.getJoystick(1);
        macropad = RobotProvider.instance.getJoystick(2);
    }

    public void run(final Context context) {
        context.takeOwnership(Robot.lights);
        // com.team766.Robot.smores
        while (true) {
            // Wait for new driver station data.
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            // Pick animation/colors.
            if (macropad.getButtonPressed(1)) {
                Robot.lights.rainbow();
            } else if (macropad.getButtonPressed(2)) {
                Robot.lights.randColor();
            } else if (macropad.getButton(3)) {
                Robot.lights.randColor();
            } else if (macropad.getButtonPressed(5)) {
                Robot.lights.fade(255, 0, 0);
            } else if (macropad.getButtonPressed(16)) {
                Robot.lights.clear();
            }

            if (macropad.getButtonPressed(4)) {
                Robot.lights.setColor(255, 255, 255);
            } else if (macropad.getButtonReleased(4)) {
                Robot.lights.clear();
            }

            // Brightness adjusting
            if (macropad.getButton(11)) {
                Robot.lights.changeBrightness(-0.01);
            }
            if (macropad.getButton(12)) {
                Robot.lights.changeBrightness(0.01);
            }

            // forward is 2, backward is 6.
            // left is 5, right is 7.
            // double forward = (macropad.getButton(2) ? 1 : 0) - (macropad.getButton(6) ? 1 : 0);
            // double left = (macropad.getButton(5) ? 1 : 0) - (macropad.getButton(7) ? 1 : 0);
            // double leftMotorPower = -left + forward;
            // double rightMotorPower = left + forward;

            // Robot.drive.setDrivePower(leftMotorPower, rightMotorPower);

            // wait for driver station data (and refresh it using the WPILib APIs)
        }
    }
}
