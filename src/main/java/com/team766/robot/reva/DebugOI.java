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
    }
}
