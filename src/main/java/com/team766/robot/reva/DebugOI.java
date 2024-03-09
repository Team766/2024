package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.OICondition;
import com.team766.framework.OIFragment;
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
 * 3 + 8/12 = Control Intake + Nudge Up/Down
 * 4 + 8/12 = Control Shooter + Nudge Up/Down
 * 13       = Intake In (default speed)
 * 14       = Intake Out (default speed)
 * 15       = Shoot (default speed)
 */
public class DebugOI extends OIFragment {
    private final JoystickReader macropad;

    private final Shoulder shoulder;
    private final Climber climber;
    private final Intake intake;
    private final Shooter shooter;
    private final OICondition controlShoulder;
    private final OICondition controlClimber;
    private final OICondition controlIntake;
    private final OICondition controlShooter;
    private final OICondition intakeIn;
    private final OICondition intakeOut;
    private final OICondition shooterShoot;

    public DebugOI(
            JoystickReader macropad,
            Shoulder shoulder,
            Climber climber,
            Intake intake,
            Shooter shooter) {
        super("DebugOI");
        this.macropad = macropad;
        this.shoulder = shoulder;
        this.climber = climber;
        this.intake = intake;
        this.shooter = shooter;

        controlShoulder =
                new OICondition(this, () -> macropad.getButton(InputConstants.CONTROL_SHOULDER));
        controlClimber =
                new OICondition(this, () -> macropad.getButton(InputConstants.CONTROL_CLIMBER));
        controlIntake =
                new OICondition(this, () -> macropad.getButton(InputConstants.CONTROL_INTAKE));
        controlShooter =
                new OICondition(this, () -> macropad.getButton(InputConstants.CONTROL_SHOOTER));
        intakeIn = new OICondition(this, () -> macropad.getButtonPressed(InputConstants.INTAKE_IN));
        intakeOut =
                new OICondition(this, () -> macropad.getButtonPressed(InputConstants.INTAKE_OUT));
        shooterShoot =
                new OICondition(
                        this, () -> macropad.getButtonPressed(InputConstants.SHOOTER_SHOOT));
    }

    @Override
    protected void handleOI(Context context) {
        // fine-grained control of the shoulder
        // used for testing and tuning
        // press down the shoulder control button and nudge the angle up and down

        if (controlShoulder.isTriggering()) {
            // Shoulder
            context.takeOwnership(shoulder);

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                shoulder.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                shoulder.nudgeDown();
            } else if (macropad.getButtonPressed(InputConstants.MACROPAD_RESET_SHOULDER)) {
                shoulder.reset();
            }
            context.releaseOwnership(shoulder);
        }

        // fine-grained control of the climber
        // used for testing and tuning
        // press down the climber control button and nudge the climber up and down

        if (controlClimber.isTriggering()) {
            // Climber
            context.takeOwnership(climber);
            climber.goNoPID();
            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                climber.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                climber.nudgeDown();
            }
            context.releaseOwnership(climber);
        } else if (controlClimber.isFinishedTriggering()) {
            context.takeOwnership(climber);
            climber.stop();
            context.releaseOwnership(climber);
        }

        // fine-grained control of the intake
        // used for testing and tuning
        // press down the intake control button and nudge ths intake speed up and down
        // < 0 - outtake
        // == 0 - stopped
        // > 0 - intake
        if (controlIntake.isTriggering()) {
            context.takeOwnership(intake);
            intake.runIntake();

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                intake.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                intake.nudgeDown();
            }
            context.releaseOwnership(intake);
        } else if (controlIntake.isFinishedTriggering()) {
            context.takeOwnership(intake);
            intake.stop();
            context.releaseOwnership(intake);
        }

        // simple one-button controls for intake
        // used for testing and tuning
        // allows for running intake at default intake/outtake speeds.
        if (intakeIn.isTriggering()) {
            context.takeOwnership(intake);
            intake.in();
            context.releaseOwnership(intake);
        } else if (intakeOut.isTriggering()) {
            context.takeOwnership(intake);
            intake.out();
            context.releaseOwnership(intake);
        } else if (intakeIn.isFinishedTriggering() || intakeOut.isFinishedTriggering()) {
            context.takeOwnership(intake);
            intake.stop();
            context.releaseOwnership(intake);
        }

        // fine-grained controls for shooter
        // used for testing and tuning
        // press down the intake control button and nudge ths shooter speed up and down
        if (controlShooter.isTriggering()) {
            context.takeOwnership(shooter);
            Robot.shooter.shoot();

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                shooter.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                shooter.nudgeDown();
            }
            context.releaseOwnership(shooter);
        } else if (controlShooter.isFinishedTriggering()) {
            context.takeOwnership(shooter);
            shooter.stop();
            context.releaseOwnership(shooter);
        }

        // simpler one-button controls for shooter
        // used for testing and tuning
        // allows for running intake at default intake/outtake speeds.
        if (shooterShoot.isTriggering()) {
            context.takeOwnership(shooter);
            shooter.shoot();
            context.releaseOwnership(shooter);
        } else if (shooterShoot.isFinishedTriggering()) {
            context.takeOwnership(shooter);
            shooter.stop();
            context.releaseOwnership(shooter);
        }
    }
}
