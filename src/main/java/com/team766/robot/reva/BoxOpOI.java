package com.team766.robot.reva;

import com.team766.framework.OIFragment;
import com.team766.framework.resources.Guarded;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.RotateToPosition;
import com.team766.robot.reva.procedures.IntakeUntilIn;

public class BoxOpOI extends OIFragment {
    private final JoystickReader gamepad;

    private final Guarded<Shoulder> shoulder;
    private final Guarded<Intake> intake;
    private final Guarded<Climber> climber;
    private final Guarded<Shooter> shooter;

    private final DeclaredCondition climberClimb;
    private final DeclaredCondition enableClimberControls;
    private final DeclaredCondition climberOverrideSoftLimits;

    public BoxOpOI(
            OI oi,
            JoystickReader gamepad,
            Guarded<Shoulder> shoulder,
            Guarded<Intake> intake,
            Guarded<Shooter> shooter,
            Guarded<Climber> climber) {
        super(oi);
        this.gamepad = gamepad;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.climber = climber;

        climberClimb = new DeclaredCondition(() ->
                Math.abs(gamepad.getAxis(InputConstants.XBOX_LS_Y)) > InputConstants.XBOX_DEADZONE
                        || Math.abs(gamepad.getAxis(InputConstants.XBOX_RS_Y))
                                > InputConstants.XBOX_DEADZONE);

        enableClimberControls = new DeclaredCondition(
                () -> gamepad.getButton(InputConstants.XBOX_A).isTriggering()
                        && gamepad.getButton(InputConstants.XBOX_B).isTriggering());

        climberOverrideSoftLimits = new DeclaredCondition(
                () -> gamepad.getButton(InputConstants.XBOX_X).isTriggering()
                        && gamepad.getButton(InputConstants.XBOX_Y).isTriggering());
    }

    @Override
    protected void dispatch() {
        // check to see if we should also disable the climber's soft limits
        if (climberOverrideSoftLimits.isNewlyTriggering()) {
            tryRunning(() -> reserve(climber).enableSoftLimits(false));
        } else if (climberOverrideSoftLimits.isFinishedTriggering()) {
            tryRunning(() -> reserve(climber).enableSoftLimits(true));
        }

        switch (enableClimberControls.getState()) {
                // shoulder positions
            case IsNotTriggering -> {
                if (gamepad.getButton(InputConstants.XBOX_A).isTriggering()) {
                    // intake
                    tryRunning(() -> reserve(shoulder).setGoal(RotateToPosition.INTAKE_FLOOR));
                } else if (gamepad.getButton(InputConstants.XBOX_B).isTriggering()) {
                    // shoot closer to speaker
                    tryRunning(() -> reserve(shoulder).setGoal(RotateToPosition.SHOOT_LOW));
                } else if (gamepad.getButton(InputConstants.XBOX_X).isTriggering()) {
                    // amp shot
                    tryRunning(() -> reserve(shoulder).setGoal(RotateToPosition.AMP));
                } else if (gamepad.getButton(InputConstants.XBOX_Y).isTriggering()) {
                    // shooter assist
                    tryRunning(() -> reserve(shoulder).setGoal(RotateToPosition.SHOOTER_ASSIST));
                    // Currently it will only modify the speed if the right trigger is already held.
                    // TODO: Make this more tolerant for when Y is pressed before right trigger.
                    if (getStatus(Shooter.Status.class).get().targetSpeed() != 0.0) {
                        tryRunning(() -> reserve(shooter)
                                .setGoal(Shooter.ShootAtSpeed.SHOOTER_ASSIST_SPEED));
                    }
                } else if (gamepad.getPOV() == 0) {
                    tryRunning(() -> reserve(shoulder).setGoal(new Shoulder.NudgeUp()));
                } else if (gamepad.getPOV() == 180) {
                    tryRunning(() -> reserve(shoulder).setGoal(new Shoulder.NudgeDown()));
                }
            }
                // climber
            case IsNewlyTriggering -> {
                // move the shoulder out of the way
                // NOTE: this happens asynchronously.
                // the boxop needs to wait for the shoulder to be fully out of the way before moving
                // the climbers.
                tryRunning(() -> reserve(shoulder).setGoal(RotateToPosition.TOP));
            }
            case IsTriggering -> {
                // if the sticks are being moving, move the corresponding climber(s)
                if (climberClimb.isTriggering()) {
                    tryReserving(climber, climber -> {
                        climber.setLeftPower(gamepad.getAxis(InputConstants.XBOX_LS_Y));
                        climber.setRightPower(gamepad.getAxis(InputConstants.XBOX_RS_Y));
                    });
                } else {
                    tryRunning(() -> reserve(climber).stop());
                }
            }
            case IsFinishedTriggering -> {
                tryRunning(() -> reserve(climber).stop());

                // restore the shoulder
                tryRunning(() -> reserve(shoulder).setGoal(new Shoulder.RotateToPosition(85)));
            }
        }

        // shooter
        if (gamepad.getAxis(InputConstants.XBOX_RT) > 0) {
            tryRunning(() -> reserve(shooter).setGoal(new Shooter.ShootAtSpeed(4800)));
        }
        byDefault(() -> reserve(shooter).setGoal(new Shooter.Stop()));

        // intake
        if (gamepad.getButton(InputConstants.XBOX_RB).isTriggering()) {
            tryRunning(() -> reserve(intake).setGoal(new Intake.Out()));
        }
        if (gamepad.getButton(InputConstants.XBOX_LB).isTriggering()) {
            tryRunning(() -> new IntakeUntilIn(reserve(intake)));
        }
        byDefault(() -> reserve(intake).setGoal(new Intake.Stop()));

        // rumble
        // if (intake.getStatus().hasNoteInIntake()) {
        //   ((GenericHID) gamepad).setRumble(RumbleType.kBothRumble, 0.5);
        // }
    }
}
