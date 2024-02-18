package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Shoulder;

public class DebugOI {
    private final JoystickReader macropad;

    private final Shoulder shoulder;

    public DebugOI(JoystickReader macropad, Shoulder shoulder) {
        this.macropad = macropad;
        this.shoulder = shoulder;
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
        } else if (macropad.getButton(InputConstants.CONTROL_CLIMBER)) {
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
        } else if (macropad.getButton(InputConstants.CONTROL_INTAKE)) {
            // Intake
            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                // context.takeOwnership(intake);
                // intake.nudgeUp();
                // context.releaseOwnership(intake);
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                // context.takeOwnership(intake);
                // intake.nudgeDown();
                // context.releaseOwnership(intake);
            }
        } else if (macropad.getButton(InputConstants.CONTROL_SHOOTER)) {
            // Shooter
            if (macropad.getButtonPressed(InputConstants.NUDGE_UP)) {
                // context.takeOwnership(shooter);
                // shooter.nudgeUp();
                // context.releaseOwnership(shooter);
            } else if (macropad.getButtonPressed(InputConstants.NUDGE_DOWN)) {
                // context.takeOwnership(shooter);
                // shooter.nudgeDown();
                // context.releaseOwnership(shooter);
            }
        }
    }
}
