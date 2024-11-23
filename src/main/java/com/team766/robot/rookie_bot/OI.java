package com.team766.robot.rookie_bot;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.example.procedures.*;


/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {
    private JoystickReader joystick0;
    private JoystickReader joystick1;
    private JoystickReader joystick2;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        joystick0 = RobotProvider.instance.getJoystick(0);
        joystick1 = RobotProvider.instance.getJoystick(1);
        joystick2 = RobotProvider.instance.getJoystick(2);
    }

    public void run(final Context context) {
        context.takeOwnership(Robot.drive);
        context.takeOwnership(Robot.intake);
        while (true) {
            // wait for driver station data (and refresh it using the WPILib APIs)

            RobotProvider.instance.refreshDriverStationData();

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.
            Robot.drive.setArcadeDrivePower(-0.5 * joystick0.getAxis(1), joystick0.getAxis(4));
            log("Elevator height: " + Robot.elevator.getElevatorDistance());
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            if (joystick0.getButtonPressed(4)) {
                Robot.elevator.moveElevator(true);
            }
            if (joystick0.getButtonPressed(1)) {
                Robot.elevator.moveElevator(false);
            }
            if (joystick0.getButtonPressed(5)) {
                Robot.intake.setintakePower(0.3, 0.75);
            }
            if (joystick0.getButtonReleased(5)) {
                Robot.intake.setintakePower(0, 0);
            }
            if (joystick0.getButtonPressed(6)) {
                Robot.intake.setintakePower(-0.13, -0.55);
            }
            if (joystick0.getButtonReleased(6)) {
                Robot.intake.setintakePower(0, 0);
            }
        }
    }
}
