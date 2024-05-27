package com.team766.robot.reva;

import com.team766.framework.OIFragment;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;

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

    public DebugOI(OI oi, JoystickReader macropad) {
        super(oi);
        this.macropad = macropad;
    }

    @Override
    protected void dispatch() {
        // fine-grained control of the shoulder
        // used for testing and tuning
        // press down the shoulder control button and nudge the angle up and down
        if (macropad.getButton(InputConstants.CONTROL_SHOULDER)) {
            if (macropad.getButton(InputConstants.NUDGE_UP)) {
                onceAvailable((Superstructure ss) -> ss.setGoal(new Shoulder.NudgeUp()));
            } else if (macropad.getButton(InputConstants.NUDGE_DOWN)) {
                onceAvailable((Superstructure ss) -> ss.setGoal(new Shoulder.NudgeDown()));
            } else if (macropad.getButton(InputConstants.MACROPAD_RESET_SHOULDER)) {
                onceAvailable((Superstructure ss) -> ss.resetShoulder());
            }
        }

        if (macropad.getButton(16)) {
            onceAvailable((Superstructure ss) -> ss.resetClimberPositions());
        }

        // fine-grained control of the climber
        // used for testing and tuning
        // press down the climber control button and nudge the climber up and down
        if (macropad.getButton(InputConstants.CONTROL_CLIMBER)) {
            if (macropad.getButton(InputConstants.NUDGE_UP)) {
                whileAvailable((Superstructure ss) -> {
                    ss.setGoal(new Climber.MotorPowers(-0.25, -0.25, true));
                });
            } else if (macropad.getButton(InputConstants.NUDGE_DOWN)) {
                whileAvailable((Superstructure ss) -> {
                    ss.setGoal(new Climber.MotorPowers(0.25, 0.25, true));
                });
            }
        } else {
            onceAvailable((Superstructure ss) -> ss.setGoal(new Climber.Stop()));
        }

        // simple one-button controls for intake
        // used for testing and tuning
        // allows for running intake at default intake/outtake speeds.
        if (macropad.getButton(InputConstants.INTAKE_IN)) {
            whileAvailable((Intake intake) -> intake.setGoal(new Intake.In()));
        } else if (macropad.getButton(InputConstants.INTAKE_OUT)) {
            whileAvailable((Intake intake) -> intake.setGoal(new Intake.Out()));
        }
        byDefault((Intake intake) -> intake.setGoal(new Intake.Stop()));

        // fine-grained controls for shooter
        // used for testing and tuning
        // press down the intake control button and nudge ths shooter speed up and down
        if (macropad.getButton(InputConstants.CONTROL_SHOOTER)) {
            onceAvailable((Shooter shooter) -> shooter.setGoal(new Shooter.Shoot()));

            if (macropad.getButton(InputConstants.NUDGE_UP)) {
                onceAvailable((Shooter shooter) -> shooter.setGoal(new Shooter.NudgeUp()));
            } else if (macropad.getButton(InputConstants.NUDGE_DOWN)) {
                onceAvailable((Shooter shooter) -> shooter.setGoal(new Shooter.NudgeDown()));
            }
        } else {
            onceAvailable((Shooter shooter) -> shooter.setGoal(new Shooter.Stop()));
        }
    }
}
