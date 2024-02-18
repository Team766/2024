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

    private final DriverOI swerveOI;
    private final JoystickReader leftJoystick;
    private final JoystickReader rightJoystick;
    private JoystickReader macropad;
    private DebugOI debugOI;

    public OI() {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        swerveOI = new DriverOI(Robot.drive, leftJoystick, rightJoystick);

        macropad = RobotProvider.instance.getJoystick(InputConstants.MACROPAD);
        debugOI = new DebugOI(macropad, Robot.shoulder);
    }

    public void run(Context context) {
        while (true) {
            context.waitFor(() -> RobotProvider.instance.hasNewDriverStationData());
            RobotProvider.instance.refreshDriverStationData();

            SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

            // Add driver controls here - make sure to take/release ownership
            // of mechanisms when appropriate.

            // Swerve OI: take input from left, right joysticks.  Control Drive.
            swerveOI.handleOI(context);
            debugOI.handleOI(context);
        }
    }
}
