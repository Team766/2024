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
    // allows soft limits to be overridden when true
    private boolean climberOverride;
    // allows climber to move
    private boolean canClimb;

    private final JoystickReader gamepad;
    // private final XboxController xboxController;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;
    private final Climber climber;

    private final OICondition shooterShoot;
    private final OICondition intakeOut;
    private final OICondition intakeIn;
    private final OICondition climberClimb;
    private final OICondition usingArms;
    private final OICondition climberCondition;

    public BoxOpOI(
            JoystickReader gamepad,
            // XboxController xboxController,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter,
            Climber climber) {
        this.gamepad = gamepad;
        /// this.xboxController = xboxController;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.climber = climber;

        shooterShoot = new OICondition(() -> gamepad.getAxis(InputConstants.XBOX_RT) > 0);
        intakeOut = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_RB));
        intakeIn = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_LB));
        climberClimb =
                new OICondition(
                        () ->
                                Math.abs(gamepad.getAxis(InputConstants.XBOX_LS_Y))
                                                > InputConstants.XBOX_DEADZONE
                                        || Math.abs(gamepad.getAxis(InputConstants.XBOX_RS_Y))
                                                > InputConstants.XBOX_DEADZONE);
        usingArms =
                new OICondition(
                        () ->
                                (gamepad.getButton(InputConstants.XBOX_A)
                                        || gamepad.getButton(InputConstants.XBOX_B)
                                        || gamepad.getButton(InputConstants.XBOX_X)
                                        || gamepad.getButton(InputConstants.XBOX_Y)
                                        || gamepad.getPOV() == 0
                                        || gamepad.getPOV() == 180));
        climberCondition =
                new OICondition(
                        () ->
                                ((gamepad.getButton(InputConstants.XBOX_A)
                                        && gamepad.getButton(InputConstants.XBOX_B))));
    }

    @Override
    protected void handleOI(Context context) {
        // climber override
        if (usingArms.isTriggering()) {
            if (usingArms.isNewlyTriggering()) {
                context.takeOwnership(shoulder);
            }

            // shoulder positions
            if (!climberOverride) {
                if (gamepad.getButtonPressed(InputConstants.XBOX_A)) {
                    // intake
                    shoulder.rotate(ShoulderPosition.SHOOT_LOW);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_B)) {
                    // shoot closer to speaker
                    shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_X)) {
                    // other shoot pos
                    shoulder.rotate(ShoulderPosition.TOP);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_Y)) {
                    // amp shot
                    shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
                } else if (gamepad.getPOV() == 0) {
                    shoulder.nudgeUp();
                } else if (gamepad.getPOV() == 180) {
                    shoulder.nudgeDown();
                }
            }
        } else if (usingArms.isFinishedTriggering()) {
            context.releaseOwnership(shoulder);
        }

        // climber condition
        if (climberCondition.isTriggering()) {
            if (climberCondition.isNewlyTriggering()) {
                context.takeOwnership(climber);
                context.takeOwnership(shoulder);
                canClimb = true;
                shoulder.rotate(105);
            }
            if (gamepad.getButtonPressed(InputConstants.XBOX_A)
                    && gamepad.getButtonPressed(InputConstants.XBOX_B)
                    && gamepad.getButtonPressed(InputConstants.XBOX_X)
                    && gamepad.getButtonPressed(InputConstants.XBOX_Y)) {

                climber.enableSoftLimits(false);
                climberOverride = true;
                context.releaseOwnership(climber);
            } else if (climberOverride) {
                climber.enableSoftLimits(true);
                context.releaseOwnership(climber);
                climberOverride = false;
            }

        } else if (climberCondition.isFinishedTriggering()) {
            canClimb = false;
            shoulder.rotate(85);
            context.releaseOwnership(shoulder);
        }

        // climber
        if (climberClimb.isTriggering() && canClimb) {
            if (climberClimb.isNewlyTriggering()) {
                context.takeOwnership(climber);
            }
            climber.setLeftPower(gamepad.getAxis(InputConstants.XBOX_LS_Y));
            climber.setRightPower(gamepad.getAxis(InputConstants.XBOX_RS_Y));
        } else if (climberClimb.isFinishedTriggering()) {
            climber.stop();
            context.releaseOwnership(climber);
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
            context.startAsync(new IntakeUntilIn());
        } else if (intakeOut.isFinishedTriggering() || intakeIn.isFinishedTriggering()) {
            intake.stop();
            context.releaseOwnership(intake);
        }

        // rumble
        if (Robot.intake.hasNoteInIntake()) {
            // xboxController.setRumble(RumbleType.kBothRumble, 0.5);
        }
    }
}
