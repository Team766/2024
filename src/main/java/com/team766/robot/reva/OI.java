package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.reva.constants.InputConstants;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {

    private final JoystickReader leftJoystick;
    private final JoystickReader rightJoystick;
    private final JoystickReader macropad;
    private final JoystickReader gamepad;
    private final DriverOI driverOI;
    private final DebugOI debugOI;
    private final BoxOpOI boxOpOI;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        macropad = RobotProvider.instance.getJoystick(InputConstants.MACROPAD);
        gamepad = RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD_X);

        driverOI =
                new DriverOI(
                        Robot.drive,
                        Robot.shoulder,
                        Robot.intake,
                        Robot.shooter,
                        leftJoystick,
                        rightJoystick);
        debugOI = new DebugOI(macropad, Robot.shoulder, Robot.climber, Robot.intake, Robot.shooter);
        boxOpOI = new BoxOpOI(gamepad, Robot.shoulder, Robot.intake, Robot.shooter, Robot.climber);
    }

    public void run(Context context) {
        while (true) {
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            // NOTE: DriverStation.getAlliance() returns Optional<Alliance>
            // SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.

            // Driver OI: take input from left, right joysticks.  control drive.
            driverOI.runOI(context);
            // Debug OI: allow for finer-grain testing of each mechanism.
            debugOI.runOI(context);

            boxOpOI.runOI(context);
        }
    }
}
