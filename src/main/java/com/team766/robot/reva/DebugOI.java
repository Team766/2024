package com.team766.robot.reva;

import com.team766.framework.OIFragment;
import com.team766.framework.resources.Guarded;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

/**
 * Programmer-centric controls to test each of our (non-drive) mechanisms.
 * Useful for tuning and for testing, eg in the pit.
 *
 * Uses a DOIO KB16 macropad, as follows:
 *
 *
 *      ┌───┬───┬───┬───┐   12<──  12<──
 *      │ 1 │ 2 │ 3 │ 4 │   ( 3 )  ( 4 )
 *      ├───┼───┼───┼───┤    -─>8  -─>8
 *      │ 5 │ 6 │ 7 │ 8 │
 *      ├───┼───┼───┼───┤
 *      │ 9 │ 10| 11│ 12|     12<──
 *      ├───┼───┼───┼───┤      (   )
 *      │ 13│ 14│ 15│ 16│      -─>8
 *      └───┴───┴───┴───┘
 *
 * 1 + 8/12 = Control Shoulder + Nudge Up/Down
 * 2 + 8/12 = Control Climber + Nudge Up/Down
 * 4 + 8/12 = Control Shooter + Nudge Up/Down
 *  3        = Intake In
 * 7        = Intake Out
 */
public class DebugOI extends OIFragment {
    private final JoystickReader macropad;

    private final Guarded<Shoulder> shoulder;
    private final Guarded<Climber> climber;
    private final Guarded<Intake> intake;
    private final Guarded<Shooter> shooter;

    public DebugOI(
            OI oi,
            JoystickReader macropad,
            Guarded<Shoulder> shoulder,
            Guarded<Climber> climber,
            Guarded<Intake> intake,
            Guarded<Shooter> shooter) {
        super(oi);
        this.macropad = macropad;
        this.shoulder = shoulder;
        this.climber = climber;
        this.intake = intake;
        this.shooter = shooter;
    }

    @Override
    protected void dispatch() {
        // fine-grained control of the shoulder
        // used for testing and tuning
        // press down the shoulder control button and nudge the angle up and down
        if (macropad.getButton(InputConstants.CONTROL_SHOULDER).isTriggering()) {
            if (macropad.getButton(InputConstants.NUDGE_UP).isNewlyTriggering()) {
                tryRunning(() -> reserve(shoulder).setGoal(new Shoulder.NudgeUp()));
            } else if (macropad.getButton(InputConstants.NUDGE_DOWN).isNewlyTriggering()) {
                tryRunning(() -> reserve(shoulder).setGoal(new Shoulder.NudgeDown()));
            } else if (macropad.getButton(InputConstants.MACROPAD_RESET_SHOULDER)
                    .isNewlyTriggering()) {
                tryRunning(() -> reserve(shoulder).reset());
            }
        }

        if (macropad.getButton(16).isNewlyTriggering()) {
            tryReserving(climber, climber -> {
                climber.resetLeftPosition();
                climber.resetRightPosition();
            });
        }

        // fine-grained control of the climber
        // used for testing and tuning
        // press down the climber control button and nudge the climber up and down
        switch (macropad.getButton(InputConstants.CONTROL_CLIMBER)) {
            case IsNewlyTriggering -> {
                tryRunning(() -> reserve(climber).enableSoftLimits(false));
            }
            case IsTriggering -> {
                if (macropad.getButton(InputConstants.NUDGE_UP).isNewlyTriggering()) {
                    tryReserving(climber, climber -> {
                        climber.setLeftPower(-0.25);
                        climber.setRightPower(-0.25);
                    });
                } else if (macropad.getButton(InputConstants.NUDGE_DOWN).isNewlyTriggering()) {
                    tryReserving(climber, climber -> {
                        climber.setLeftPower(0.25);
                        climber.setRightPower(0.25);
                    });
                }
            }
            case IsFinishedTriggering -> {
                tryReserving(climber, climber -> {
                    climber.stop();
                    climber.enableSoftLimits(true);
                });
            }
            default -> {}
        }

        // simple one-button controls for intake
        // used for testing and tuning
        // allows for running intake at default intake/outtake speeds.
        if (macropad.getButton(InputConstants.INTAKE_IN).isNewlyTriggering()) {
            tryRunning(() -> reserve(intake).setGoal(new Intake.In()));
        } else if (macropad.getButton(InputConstants.INTAKE_OUT).isNewlyTriggering()) {
            tryRunning(() -> reserve(intake).setGoal(new Intake.Out()));
        }
        byDefault(() -> reserve(intake).setGoal(new Intake.Stop()));

        // fine-grained controls for shooter
        // used for testing and tuning
        // press down the intake control button and nudge ths shooter speed up and down
        switch (macropad.getButton(InputConstants.CONTROL_SHOOTER)) {
            case IsNewlyTriggering -> {
                tryRunning(() -> reserve(shooter).setGoal(new Shooter.Shoot()));
            }
            case IsTriggering -> {
                if (macropad.getButton(InputConstants.NUDGE_UP).isNewlyTriggering()) {
                    tryRunning(() -> reserve(shooter).setGoal(new Shooter.NudgeUp()));
                } else if (macropad.getButton(InputConstants.NUDGE_DOWN).isNewlyTriggering()) {
                    tryRunning(() -> reserve(shooter).setGoal(new Shooter.NudgeDown()));
                }
            }
            case IsFinishedTriggering -> {
                tryRunning(() -> reserve(shooter).setGoal(new Shooter.Stop()));
            }
            default -> {}
        }
    }
}
