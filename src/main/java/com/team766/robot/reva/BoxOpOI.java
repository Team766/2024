package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.common.constants.ControlConstants;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;
import com.team766.robot.reva.procedures.IntakeUntilIn;

public class BoxOpOI extends OIFragment {
    private final JoystickReader gamepad;
    // private final XboxController xboxController;

    private final Shoulder shoulder;
    private final Intake intake;

    private final Climber climber;
    private final Shooter shooter;

    private final OICondition shooterShoot;
    private final OICondition intakeOut;
    private final OICondition intakeIn;
    private final OICondition climberClimb;
    private final OICondition moveShoulder;
    private final OICondition enableClimberControls;
    private final OICondition climberOverrideSoftLimits;

    // private final OICondition shootoi;

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

        // shootoi = new OICondition(() -> gamepad.getPOV()==270);
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

        if (!enableClimberControls.isTriggering()) {
            if (moveShoulder.isTriggering()) {
                // if (moveShoulder.isNewlyTriggering()) {
                //     context.takeOwnership(shoulder);
                // }

                if (gamepad.getButtonPressed(InputConstants.XBOX_A)) {
                    // intake
                    context.takeOwnership(shoulder);
                    shoulder.rotate(ShoulderPosition.INTAKE_FLOOR);
                    context.releaseOwnership(shoulder);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_B)) {
                    // shoot closer to speaker
                    context.takeOwnership(shoulder);
                    shoulder.rotate(ShoulderPosition.SHOOT_LOW);
                    context.releaseOwnership(shoulder);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_X)) {
                    // amp shot
                    context.takeOwnership(shoulder);
                    shoulder.rotate(ShoulderPosition.AMP);
                    context.releaseOwnership(shoulder);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_Y)) {
                    // shooter assist
                    context.takeOwnership(shoulder);
                    shoulder.rotate(ShoulderPosition.SHOOTER_ASSIST);
                    // Currently it will only modify the speed if the right trigger is already held.
                    // TODO: Make this more tolerant for when Y is pressed before right trigger.
                    if (shooter.getShouldRun()) {
                        context.takeOwnership(shooter);
                        shooter.shoot(Shooter.SHOOTER_ASSIST_SPEED);
                        context.releaseOwnership(shooter);
                    }
                    context.releaseOwnership(shoulder);
                } else if (gamepad.getPOV() == 0) {
                    context.takeOwnership(shoulder);
                    shoulder.nudgeUp();
                    context.releaseOwnership(shoulder);
                } else if (gamepad.getPOV() == 180) {
                    context.takeOwnership(shoulder);
                    shoulder.nudgeDown();
                    context.releaseOwnership(shoulder);
                }
            } /*else if (moveShoulder.isFinishedTriggering()) {
                  context.releaseOwnership(shoulder);
              } */
        }

        // if(shootoi.isTriggering()){
        //     if(shootoi.isNewlyTriggering()){
        //         context.takeOwnership(shooter);
        //     }

        //     if(gamepad.getPOV()== 270){
        //         shooter.shoot(3000);
        //     }
        // } else if (shootoi.isFinishedTriggering()){
        //     context.releaseOwnership(shooter);
        // }

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

            // if the sticks are being moving, move the corresponding climber(s)
            if (climberClimb.isTriggering()) {
                climber.setLeftPower(
                        createJoystickDeadzone(gamepad.getAxis(InputConstants.XBOX_LS_Y)));
                climber.setRightPower(
                        createJoystickDeadzone(gamepad.getAxis(InputConstants.XBOX_RS_Y)));
            } else {
                climber.stop();
            }
        } else if (enableClimberControls.isFinishedTriggering()) {
            context.releaseOwnership(climber);
            climber.stop();

            // restore the shoulder
            context.takeOwnership(shoulder);
            shoulder.rotate(85);
            context.releaseOwnership(shoulder);
        }

        // check to see if we should also disable the climber's soft limits
        if (climberOverrideSoftLimits.isNewlyTriggering()) {
            context.takeOwnership(climber);
            climber.enableSoftLimits(false);
        } else if (climberOverrideSoftLimits.isFinishedTriggering()) {
            climber.enableSoftLimits(true);
            context.releaseOwnership(climber);
        }

        // shooter
        if (shooterShoot.isNewlyTriggering()) {
            context.takeOwnership(shooter);
            shooter.shoot(4800);
            context.releaseOwnership(shooter);
        } else if (shooterShoot.isFinishedTriggering()) {
            context.takeOwnership(shooter);
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
            context.takeOwnership(intake);
            intake.stop();
            context.releaseOwnership(intake);
        }

        // rumble
        // if (intake.hasNoteInIntake()) {
        //   ((GenericHID) gamepad).setRumble(RumbleType.kBothRumble, 0.5);
        // }
    }

    /**
     * Helper method to ignore joystick values below JOYSTICK_DEADZONE
     * @param joystickValue the value to trim
     * @return the trimmed joystick value
     */
    private double createJoystickDeadzone(double joystickValue) {
        return Math.abs(joystickValue) > ControlConstants.JOYSTICK_DEADZONE ? joystickValue : 0;
    }
}
