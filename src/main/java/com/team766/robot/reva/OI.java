package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.common.DriverOI;
import com.team766.robot.reva.constants.InputConstants;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends Procedure {

    private final JoystickReader leftJoystick;
    private final JoystickReader rightJoystick;
    private final JoystickReader macropad;
    private final DriverOI driverOI;
    private final DebugOI debugOI;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        macropad = RobotProvider.instance.getJoystick(InputConstants.MACROPAD);

        driverOI = new DriverOI(Robot.drive, leftJoystick, rightJoystick);
        debugOI = new DebugOI(macropad, Robot.shoulder, Robot.intake, Robot.shooter);
    }

    public void run(Context context) {
        while (true) {
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.

            // Driver OI: take input from left, right joysticks.  control drive.
            driverOI.handleOI(context);
            // Debug OI: allow for finer-grain testing of each mechanism.
            debugOI.handleOI(context);
        }
    }
}
