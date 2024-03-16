package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Climber.ClimberPosition;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;
import com.team766.robot.reva.procedures.AutoIntake;

public class BoxOpOI extends OIFragment {
    private final JoystickReader gamepad;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;
    private final Climber climber;

    private final OICondition shooterShoot;
    private final OICondition intakeOut;
    private final OICondition intakeIn;

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

        shooterShoot = new OICondition(() -> gamepad.getAxis(InputConstants.XBOX_RT) > 0);
        intakeOut = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_RB));
        intakeIn = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_LB));
    }

    @Override
    protected void handleOI(Context context) {
        // shoulder positions
        if (gamepad.getButtonPressed(InputConstants.XBOX_A)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
            context.releaseOwnership(shoulder);
        } else if (gamepad.getButtonPressed(InputConstants.XBOX_B)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
            context.releaseOwnership(shoulder);
        } else if (gamepad.getButtonPressed(InputConstants.XBOX_X)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.TOP);
            context.releaseOwnership(shoulder);
        } else if (gamepad.getButtonPressed(InputConstants.XBOX_Y)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.SHOOT_LOW);
            context.releaseOwnership(shoulder);
        }

        // dpad
        // shoulder up 5
        if (gamepad.getPOV() == 0) {
            context.takeOwnership(shoulder);
            shoulder.nudgeUp();
            context.releaseOwnership(shoulder);
        }

        if (gamepad.getPOV() == 180) {
            context.takeOwnership(shoulder);
            shoulder.nudgeDown();
            context.releaseOwnership(shoulder);
        }

        // climber
        if (gamepad.getPOV() == 90) {
            context.takeOwnership(climber);
            climber.setHeight(ClimberPosition.BOTTOM);
            context.releaseOwnership(climber);
        }

        if (gamepad.getPOV() == 360) {
            context.takeOwnership(climber);
            climber.setHeight(ClimberPosition.TOP);
            context.releaseOwnership(climber);
        }

        if (gamepad.getButton(9) || gamepad.getButton(10)) {
            context.takeOwnership(climber);
            if (gamepad.getAxis(1) < 0) {
                climber.nudgeUp();
            } else if (gamepad.getAxis(1) > 0) {
                climber.nudgeDown();
            } else {
                context.releaseOwnership(climber);
            }
        }

        // shooter
        if (shooterShoot.isNewlyTriggering()) {
            context.takeOwnership(shooter);
            shooter.shoot();
        } else if (shooterShoot.isFinishedTriggering()) {
            shooter.stop();
            context.releaseOwnership(shooter);
        }

        // intake
        if (intakeOut.isNewlyTriggering()) {
            context.takeOwnership(intake);
            intake.out();
        } else if (intakeIn.isNewlyTriggering()) {
            context.takeOwnership(intake);
            new AutoIntake().run(context);
        } else if (intakeOut.isFinishedTriggering() || intakeIn.isFinishedTriggering()) {
            intake.stop();
            context.releaseOwnership(intake);
        }
    }
}
