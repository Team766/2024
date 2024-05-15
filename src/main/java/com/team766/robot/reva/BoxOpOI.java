package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
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
    private final Lights lights;

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
            Climber climber,
            Lights lights) {
        this.gamepad = gamepad;
        /// this.xboxController = xboxController;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.climber = climber;
        this.lights = lights;

        // shootoi = new OICondition(() -> gamepad.getPOV()==270);
        intakeOut = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_RB));
        intakeIn = new OICondition(() -> gamepad.getButton(InputConstants.XBOX_LB));
        shooterShoot = new OICondition(() -> gamepad.getAxis(InputConstants.XBOX_RT) > 0);
        climberClimb = new OICondition(() ->
                Math.abs(gamepad.getAxis(InputConstants.XBOX_LS_Y)) > InputConstants.XBOX_DEADZONE
                        || Math.abs(gamepad.getAxis(InputConstants.XBOX_RS_Y))
                                > InputConstants.XBOX_DEADZONE);

        moveShoulder = new OICondition(() -> (gamepad.getButton(InputConstants.XBOX_A)
                || gamepad.getButton(InputConstants.XBOX_B)
                || gamepad.getButton(InputConstants.XBOX_X)
                || gamepad.getButton(InputConstants.XBOX_Y)
                || gamepad.getPOV() == 0
                || gamepad.getPOV() == 180));
        enableClimberControls = new OICondition(() -> ((gamepad.getButton(InputConstants.XBOX_A)
                && gamepad.getButton(InputConstants.XBOX_B))));

        climberOverrideSoftLimits = new OICondition(() -> (gamepad.getButton(InputConstants.XBOX_X)
                && gamepad.getButton(InputConstants.XBOX_Y)));
    }

    @Override
    protected void handleOI(Context context) {
        // shoulder positions

        if (!enableClimberControls.isTriggering()) {
            if (moveShoulder.isTriggering()) {
                if (gamepad.getButtonPressed(InputConstants.XBOX_A)) {
                    // intake
                    shoulder.rotate(ShoulderPosition.INTAKE_FLOOR);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_B)) {
                    // shoot closer to speaker
                    shoulder.rotate(ShoulderPosition.SHOOT_LOW);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_X)) {
                    // amp shot
                    shoulder.rotate(ShoulderPosition.AMP);
                } else if (gamepad.getButtonPressed(InputConstants.XBOX_Y)) {
                    // shooter assist
                    shoulder.rotate(ShoulderPosition.SHOOTER_ASSIST);
                    // Currently it will only modify the speed if the right trigger is already held.
                    // TODO: Make this more tolerant for when Y is pressed before right trigger.
                    if (shooter.getShouldRun()) {
                        shooter.shoot(Shooter.SHOOTER_ASSIST_SPEED);
                    }
                } else if (gamepad.getPOV() == 0) {
                    shoulder.nudgeUp();
                } else if (gamepad.getPOV() == 180) {
                    shoulder.nudgeDown();
                }
            }
        }

        // if(shootoi.isTriggering()){
        //     if(gamepad.getPOV()== 270){
        //         shooter.shoot(3000);
        //     }
        // }

        // climber
        if (enableClimberControls.isTriggering()) {
            if (enableClimberControls.isNewlyTriggering()) {
                // move the shoulder out of the way
                // NOTE: this happens asynchronously.
                // the boxop needs to wait for the shoulder to be fully out of the way before moving
                // the climbers.
                shoulder.rotate(ShoulderPosition.TOP);
            }

            // if the sticks are being moving, move the corresponding climber(s)
            if (climberClimb.isTriggering()) {
                climber.setLeftPower(gamepad.getAxis(InputConstants.XBOX_LS_Y));
                climber.setRightPower(gamepad.getAxis(InputConstants.XBOX_RS_Y));
            } else {
                climber.stop();
            }
        } else if (enableClimberControls.isFinishedTriggering()) {
            climber.stop();

            // restore the shoulder
            shoulder.rotate(85);
        }

        // check to see if we should also disable the climber's soft limits
        if (climberOverrideSoftLimits.isNewlyTriggering()) {
            climber.enableSoftLimits(false);
        } else if (climberOverrideSoftLimits.isFinishedTriggering()) {
            climber.enableSoftLimits(true);
        }

        // shooter
        if (shooterShoot.isNewlyTriggering()) {
            shooter.shoot(4800);
        } else if (shooterShoot.isFinishedTriggering()) {
            shooter.stop();
        }

        // intake
        if (intakeOut.isNewlyTriggering()) {
            intake.out();
        } else if (intakeIn.isNewlyTriggering()) {
            context.startAsync(new IntakeUntilIn(intake, lights));
        } else if (intakeOut.isFinishedTriggering() || intakeIn.isFinishedTriggering()) {
            intake.stop();
        }

        // rumble
        // if (intake.hasNoteInIntake()) {
        //   ((GenericHID) gamepad).setRumble(RumbleType.kBothRumble, 0.5);
        // }
    }
}
