package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;

public class BoxOpOI extends OIFragment {
    private final JoystickReader gamepad;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;
    private final Climber climber;

    public BoxOpOI(
            JoystickReader gamepad,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter,
            Climber climber) {
        super("BoxOpOI");
        this.gamepad = gamepad;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.climber = climber;

        // shoulder positions
        addRule(
                () -> gamepad.getButtonPressed(InputConstants.XBOX_A),
                (context) -> moveShoulder(context, ShoulderPosition.SHOOT_MEDIUM),
                null);

        addRule(
                () -> gamepad.getButtonPressed(InputConstants.XBOX_B),
                (context) -> moveShoulder(context, ShoulderPosition.BOTTOM),
                null);

        addRule(
                () -> gamepad.getButtonPressed(InputConstants.XBOX_X),
                (context) -> moveShoulder(context, ShoulderPosition.TOP),
                null);

        addRule(
                () -> gamepad.getButtonPressed(InputConstants.XBOX_Y),
                (context) -> moveShoulder(context, ShoulderPosition.SHOOT_LOW),
                null);

        addRule(
                () -> gamepad.getPOV() == 0,
                (context) -> nudgeShoulder(context, true /* up */),
                null);

        addRule(
                () -> gamepad.getPOV() == 180,
                (context) -> nudgeShoulder(context, false /* down */),
                null);

        // TODO: control climber up and down
        addRule(() -> gamepad.getPOV() == 90, null, null);
        addRule(() -> gamepad.getPOV() == 360, null, null);

        // shooter
        addRule(
                () -> gamepad.getAxis(InputConstants.XBOX_RT) > 0,
                (context) -> shoot(context),
                (context) -> shootDone(context));

        // intake
        addRule(
                () -> gamepad.getButton(InputConstants.XBOX_RB),
                (context) -> intakeOut(context),
                (context) -> intakeStop(context));

        addRule(
                () -> gamepad.getButton(InputConstants.XBOX_LB),
                (context) -> intakeIn(context),
                (context) -> intakeStop(context));
    }

    public void moveShoulder(Context context, ShoulderPosition position) {
        context.takeOwnership(shoulder);
        shoulder.rotate(position);
        context.releaseOwnership(shoulder);
    }

    public void nudgeShoulder(Context context, boolean up) {
        context.takeOwnership(shoulder);
        if (up) {
            shoulder.nudgeUp();
        } else {
            shoulder.nudgeDown();
        }
        context.releaseOwnership(shoulder);
    }

    public void shoot(Context context) {
        context.takeOwnership(shooter);
        shooter.shoot();
        context.releaseOwnership(shooter);
    }

    public void shootDone(Context context) {
        context.takeOwnership(shooter);
        shooter.stop();
        context.releaseOwnership(shooter);
    }

    public void intakeIn(Context context) {
        context.takeOwnership(intake);
        intake.in();
        context.releaseOwnership(intake);
    }

    public void intakeOut(Context context) {
        context.takeOwnership(intake);
        intake.out();
        context.releaseOwnership(intake);
    }

    public void intakeStop(Context context) {
        context.takeOwnership(intake);
        intake.stop();
        context.releaseOwnership(intake);
    }
}
