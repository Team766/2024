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
import com.team766.robot.reva.procedures.IntakeUntilIn;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public class BoxOpOI extends OIFragment {
    // allows soft limits to be overridden when true 
    private boolean climberOverride;
    // allows climber to move
    private boolean canClimb;

    private final JoystickReader gamepad;
    // private final XboxController xboxController;

    private final Shoulder shoulder;
    private final Intake intake;

    private final Climber climber;
    private final Shooter shooter;

    private final OICondition intakeOut;
    private final OICondition intakeIn;
    private final OICondition climberClimb;
    private final OICondition shooterShoot;
    private final OICondition moveShoulder;
    private final OICondition enableClimberControls;
    private final OICondition climberOverrideSoftLimits;

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

        moveShoulder =
                new OICondition(
                        () ->
                                (gamepad.getButton(InputConstants.XBOX_A)
                                        || gamepad.getButton(InputConstants.XBOX_B)
                                        || gamepad.getButton(InputConstants.XBOX_X)
                                        || gamepad.getButton(InputConstants.XBOX_Y)
                                        || gamepad.getPOV() == 0
                                        || gamepad.getPOV() == 180));
        enableClimberControls =
                new OICondition(
                        () ->
                                ((gamepad.getButton(InputConstants.XBOX_A)
                                        && gamepad.getButton(InputConstants.XBOX_B))));

        climberOverrideSoftLimits =
                new OICondition(
                        () ->
                                (gamepad.getButton(InputConstants.XBOX_X)
                                        && gamepad.getButton(InputConstants.XBOX_Y)));
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

        // shooter
        if (shooterShoot.isNewlyTriggering()) {
            context.takeOwnership(shooter);
            shooter.shoot(5600);
            context.releaseOwnership(shooter);
        } else if (shooterShoot.isFinishedTriggering()) {
            context.takeOwnership(shooter);
            shooter.stop();
            context.releaseOwnership(shooter);
        }

        // climber
        if (enableClimberControls.isTriggering()) {
            if (enableClimberControls.isNewlyTriggering()) {
                context.takeOwnership(climber);
                // move the shoulder out of the way
                // NOTE: this happens asynchronously.
                // the boxop needs to wait for the shoulder to be fully out of the way before moving
                // the climbers.
                context.takeOwnership(shoulder);
                shoulder.rotate(ShoulderPosition.TOP);
                context.releaseOwnership(shoulder);
            }

            // check to see if we should also disable the climber's soft limits
            if (climberOverrideSoftLimits.isNewlyTriggering()) {
                climber.enableSoftLimits(false);
            } else if (climberOverrideSoftLimits.isFinishedTriggering()) {
                climber.enableSoftLimits(true);
            }

            // if the sticks are being moving, move the corresponding climber(s)
            if (climberClimb.isTriggering()) {
                climber.setLeftPower(gamepad.getAxis(InputConstants.XBOX_LS_Y));
                climber.setRightPower(gamepad.getAxis(InputConstants.XBOX_RS_Y));
            }
        } else if (enableClimberControls.isFinishedTriggering()) {
            context.releaseOwnership(climber);

            // restore the shoulder
            context.takeOwnership(shoulder);
            shoulder.rotate(85);
            context.releaseOwnership(shoulder);
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

        // rumble
        // if (intake.hasNoteInIntake()) {
        //   ((GenericHID) gamepad).setRumble(RumbleType.kBothRumble, 0.5);
        // }



    }
}
