package com.team766.robot.burro_elevator;

import static com.team766.robot.burro_elevator.constants.InputConstants.*;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.burro_elevator.procedures.*;

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
        context.takeOwnership(Robot.elevator);
        context.takeOwnership(Robot.gripper);

        while (true) {
            // wait for driver station data (and refresh it using the WPILib APIs)
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.

            Robot.drive.drive(
                    -joystick0.getAxis(AXIS_FORWARD_BACKWARD) * 0.5, -joystick0.getAxis(AXIS_TURN) * 0.5);

            if (joystick0.getButton(BUTTON_ELEVATOR_UP)) {
                Robot.elevator.setPosition(Robot.elevator.getPosition() + NUDGE_UP_INCREMENT);
            } else if (joystick0.getButton(BUTTON_ELEVATOR_DOWN)) {
                Robot.elevator.setPosition(Robot.elevator.getPosition() - NUDGE_DOWN_INCREMENT);
            }
            // if (joystick0.getButton(BUTTON_ELEVATOR_UP)) {
            //     Robot.elevator.setPower(0.2);
            // } else if (joystick0.getButton(BUTTON_ELEVATOR_DOWN)) {
            //     Robot.elevator.setPower(-0.2);
            // } else {
            //     Robot.elevator.setPower(0);
            // }

            if (joystick0.getButton(BUTTON_INTAKE)) {
                Robot.gripper.intake();
            } else if (joystick0.getButton(BUTTON_OUTTAKE)) {
                Robot.gripper.outtake();
            } else {
                Robot.gripper.idle();
            }
        }
    }
}
