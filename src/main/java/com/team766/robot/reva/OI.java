package com.team766.robot.reva;

import com.team766.framework.OIBase;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

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

    public OI(
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            Climber climber,
            Lights lights,
            ForwardApriltagCamera forwardAprilTagCamera) {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(null, InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(null, InputConstants.RIGHT_JOYSTICK);
        macropad = RobotProvider.instance.getJoystick(null, InputConstants.MACROPAD);
        gamepad = RobotProvider.instance.getJoystick(null, InputConstants.BOXOP_GAMEPAD_X);

        driverOI =
                new DriverOI(
                        this,
                        drive,
                        shoulder,
                        intake,
                        shooter,
                        lights,
                        forwardAprilTagCamera,
                        leftJoystick,
                        rightJoystick);
        debugOI = new DebugOI(macropad, shoulder, climber, intake, shooter);
        boxOpOI = new BoxOpOI(gamepad, shoulder, intake, shooter, climber, lights);
    }

    @Override
    protected void dispatch() {
        // NOTE: DriverStation.getAlliance() returns Optional<Alliance>
        // SmartDashboard.putString("Alliance", DriverStation.getAlliance().toString());

        // Add driver controls here.

        // Driver OI: take input from left, right joysticks.  control drive.
        driverOI.run();
        // Debug OI: allow for finer-grain testing of each mechanism.
        debugOI.run();

        boxOpOI.run();

        byDefault(intake.setGoalBehavior(new Intake.StopIntake()));
    }
}
