package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.procedures.FinishIntakeAndShoot;

public class DebugOI {
    private final JoystickReader macropad;

    private final Shoulder shoulder;
    private final Climber climber;
    private final Intake intake;
    private final Shooter shooter;

    public DebugOI(
            JoystickReader macropad,
            Shoulder shoulder,
            Climber climber,
            Intake intake,
            Shooter shooter) {
        this.macropad = macropad;
        this.shoulder = shoulder;
        this.climber = climber;
        this.intake = intake;
        this.shooter = shooter;
    }

    public void handleOI(Context context) {
        if (macropad.getButton(InputConstants.CONTROL_SHOULDER)) {
            // Shoulder
            context.takeOwnership(shoulder);

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                shoulder.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                shoulder.nudgeDown();
            } else if (macropad.getButtonPressed(9)) {
                shoulder.reset();
            }
            context.releaseOwnership(shoulder);
        }
        if (macropad.getButton(InputConstants.CONTROL_CLIMBER)) {
            // Climber
            context.takeOwnership(climber);
            climber.goNoPID();
            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                climber.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                climber.nudgeDown();
            }
            context.releaseOwnership(climber);
        } else if (climber.isRunningNoPID()) {
            context.takeOwnership(climber);
            climber.stop();
            context.releaseOwnership(climber);
        }

        if (macropad.getButton(InputConstants.CONTROL_INTAKE)) {
            context.takeOwnership(intake);
            intake.runIntake();

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                intake.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                intake.nudgeDown();
            }

            if (macropad.getButtonPressed(InputConstants.MACROPAD_PRESET_1)) {
                intake.in();
            } else if (macropad.getButtonPressed(InputConstants.MACROPAD_PRESET_2)) {
                intake.out();
            }

            context.releaseOwnership(intake);
        } else if (intake.getState() != Intake.State.STOPPED) {
            context.takeOwnership(intake);
            intake.stop();
            context.releaseOwnership(intake);
        }

        if (macropad.getButton(InputConstants.CONTROL_SHOOTER)) {
            context.takeOwnership(shooter);
            Robot.shooter.runShooter();

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                shooter.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                shooter.nudgeDown();
            }
            if (macropad.getButtonPressed(InputConstants.MACROPAD_PRESET_3)) {
                new FinishIntakeAndShoot().run(context);
            }
            context.takeOwnership(shooter);
        } else {
            context.takeOwnership(shooter);
            shooter.stop();
            context.takeOwnership(shooter);
        }
    }
}
