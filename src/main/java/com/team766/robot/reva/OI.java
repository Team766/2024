package com.team766.robot.reva;

import com.team766.framework.OIBase;
import com.team766.framework.resources.Guarded;
import com.team766.hal.JoystickReader;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.ForwardApriltagCamera;
import com.team766.robot.reva.mechanisms.Intake;
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
    private final Guarded<Intake> intake;

    public OI(
            Drive drive,
            Shoulder shoulder,
            Shooter shooter,
            Intake intake,
            Climber climber,
            ForwardApriltagCamera forwardAprilTagCamera) {
        loggerCategory = Category.OPERATOR_INTERFACE;

        leftJoystick = RobotProvider.instance.getJoystick(this, InputConstants.LEFT_JOYSTICK);
        rightJoystick = RobotProvider.instance.getJoystick(this, InputConstants.RIGHT_JOYSTICK);
        macropad = RobotProvider.instance.getJoystick(this, InputConstants.MACROPAD);
        gamepad = RobotProvider.instance.getJoystick(this, InputConstants.BOXOP_GAMEPAD_X);
        this.intake = guard(intake);

        driverOI = new DriverOI(
                this,
                guard(drive),
                guard(shoulder),
                guard(intake),
                guard(shooter),
                guard(forwardAprilTagCamera),
                leftJoystick,
                rightJoystick);
        debugOI = new DebugOI(
                this, macropad, guard(shoulder), guard(climber), guard(intake), guard(shooter));
        boxOpOI = new BoxOpOI(
                this, gamepad, guard(shoulder), guard(intake), guard(shooter), guard(climber));
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

        byDefault(() -> reserve(intake).setGoal(new Intake.Stop()));
    }
}
