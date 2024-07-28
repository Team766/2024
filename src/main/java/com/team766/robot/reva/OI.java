package com.team766.robot.reva;

import com.team766.framework.OIBase;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the code that allow control of the robot.
 */
public class OI extends OIBase {

    private final JoystickReader leftJoystick;
    private final JoystickReader rightJoystick;
    private final JoystickReader macropad;
    private final JoystickReader gamepad;
    private final DriverOI driverOI;
    private final DebugOI debugOI;
    private final BoxOpOI boxOpOI;

    public OI(Drive drive, Superstructure ss, Intake intake, Shooter shooter) {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(InputConstants.RIGHT_JOYSTICK);
        macropad = RobotProvider.instance.getJoystick(InputConstants.MACROPAD);
        gamepad = RobotProvider.instance.getJoystick(InputConstants.BOXOP_GAMEPAD_X);

        driverOI = new DriverOI(this, leftJoystick, rightJoystick, drive, ss, intake);
        debugOI = new DebugOI(this, macropad, ss, intake, shooter);
        boxOpOI = new BoxOpOI(this, gamepad, ss, intake, shooter);
    }

    @Override
    protected void dispatch() {
        // NOTE: DriverStation.getAlliance() returns Optional<Alliance>
        // SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

        // Add driver controls here.

        // Driver OI: take input from left, right joysticks.  control drive.
        driverOI.run();

        boxOpOI.run();

        // Debug OI: allow for finer-grain testing of each mechanism.
        debugOI.run();
    }
}
