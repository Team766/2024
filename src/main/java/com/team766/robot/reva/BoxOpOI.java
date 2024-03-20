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
import com.team766.robot.reva.procedures.IntakeUntilIn;

public class BoxOpOI extends OIFragment {
    private final JoystickReader gamepad;

    private final Shoulder shoulder;
    private final Intake intake;

    private final Climber climber;
    private final Shooter shooter;

    private final OICondition intakeOut;
    private final OICondition intakeIn;
    private final OICondition climberClimb;
    private final OICondition shooterShoot;

    public BoxOpOI(
            JoystickReader gamepad,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter,
            Climber climber) {
        this.gamepad = gamepad;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.climber = climber;

        intakeOut = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_RB));
        intakeIn = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_LB));
        shooterShoot = new OICondition(() -> gamepad.getAxis(InputConstants.XBOX_RT) > 0);
        climberClimb =
                new OICondition(
                        () ->
                                Math.abs(gamepad.getAxis(InputConstants.XBOX_LS_Y))
                                                > InputConstants.XBOX_DEADZONE
                                        || Math.abs(gamepad.getAxis(InputConstants.XBOX_RS_Y))
                                                > InputConstants.XBOX_DEADZONE);
    }

    @Override
    protected void handleOI(Context context) {
        // shoulder positions
        if (gamepad.getButtonPressed(InputConstants.XBOX_A)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.BOTTOM);
            context.releaseOwnership(shoulder);
        } else if (gamepad.getButtonPressed(InputConstants.XBOX_B)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
            context.releaseOwnership(shoulder);
        } else if (gamepad.getButtonPressed(InputConstants.XBOX_X)) {
            context.takeOwnership(shoulder);
            context.takeOwnership(shooter);
            shoulder.rotate(ShoulderPosition.AMP);
            shooter.shoot(3000);
            context.releaseOwnership(shooter);
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

        // shooter
        if (shooterShoot.isNewlyTriggering()) {
            context.takeOwnership(shooter);
            shooter.shoot();
            context.releaseOwnership(shooter);
        } else if (shooterShoot.isFinishedTriggering()) {
            context.takeOwnership(shooter);
            shooter.shoot(0);
            context.takeOwnership(shooter);
        }

        // climber
        if (climberClimb.isTriggering()) {
            if (climberClimb.isNewlyTriggering()) {
                context.takeOwnership(climber);
            }
            climber.setLeftPower(gamepad.getAxis(InputConstants.XBOX_LS_Y));
            climber.setRightPower(gamepad.getAxis(InputConstants.XBOX_RS_Y));
        } else if (climberClimb.isFinishedTriggering()) {
            climber.stop();
            context.releaseOwnership(climber);
        }

        // intake
        if (intakeOut.isNewlyTriggering()) {
            context.takeOwnership(intake);
            intake.out();
        } else if (intakeIn.isNewlyTriggering()) {
            context.takeOwnership(intake);
            context.startAsync(new IntakeUntilIn());
        } else if (intakeOut.isFinishedTriggering() || intakeIn.isFinishedTriggering()) {
            context.takeOwnership(intake);
            intake.stop();
            context.releaseOwnership(intake);
        }
    }
}
