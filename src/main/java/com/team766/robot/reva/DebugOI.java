package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

public class DebugOI {
    private final JoystickReader macropad;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;

    public DebugOI(JoystickReader macropad, Shoulder shoulder, Intake intake, Shooter shooter) {
        this.macropad = macropad;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
    }

    public void handleOI(Context context) {
        if (macropad.getButton(InputConstants.CONTROL_SHOULDER)) {
            // Shoulder
            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                context.takeOwnership(shoulder);
                shoulder.nudgeUp();
                context.releaseOwnership(shoulder);
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                context.takeOwnership(shoulder);
                shoulder.nudgeDown();
                context.releaseOwnership(shoulder);
            }
        }
        if (macropad.getButton(InputConstants.CONTROL_CLIMBER)) {
            // Climber
            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                // context.takeOwnership(climber);
                // climber.nudgeUp();
                // context.releaseOwnership(climber);
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                // context.takeOwnership(climber);
                // climber.nudgeDown();
                // context.releaseOwnership(climber);
            }
        }

        if (macropad.getButton(InputConstants.CONTROL_INTAKE)) {
            context.takeOwnership(Robot.intake);
            Robot.intake.in();

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                Robot.intake.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                Robot.intake.nudgeDown();
            }
            context.releaseOwnership(Robot.intake);
        } else {
            context.takeOwnership(Robot.intake);
            Robot.intake.stop();
            context.releaseOwnership(Robot.intake);
        }

        if (macropad.getButton(InputConstants.CONTROL_SHOOTER)) {
            context.takeOwnership(Robot.shooter);
            Robot.shooter.shoot();

            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                Robot.shooter.nudgeUp();
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                Robot.shooter.nudgeDown();
            }
            context.takeOwnership(Robot.shooter);
        } else {
            context.takeOwnership(Robot.shooter);
            Robot.shooter.stop();
            context.takeOwnership(Robot.shooter);
        }
    }
}
