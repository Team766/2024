package com.team766.robot.smores;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.robot.smores.mechanisms.Lights;

public class LightsOI {
    private final JoystickReader macropad;

    private final Lights lights;

    public LightsOI(JoystickReader macropad, Lights lights) {
        this.macropad = macropad;
        this.lights = lights;
    }

    public void handleOI(Context context) {
        // Pick animation/colors.
        if (macropad.getButtonPressed(1)) {
            lights.rainbow();
        } else if (macropad.getButtonPressed(2)) {
            lights.randColor();
        } else if (macropad.getButton(3)) {
            lights.randColor();
        } else if (macropad.getButtonPressed(5)) {
            lights.fade(255, 0, 0);
        } else if (macropad.getButtonPressed(16)) {
            lights.clear();
        }

        if (macropad.getButtonPressed(4)) {
            lights.setColor(255, 255, 255);
        } else if (macropad.getButtonReleased(4)) {
            lights.clear();
        }

        // Brightness adjusting
        if (macropad.getButton(11)) {
            lights.changeBrightness(-0.01);
        }
        if (macropad.getButton(12)) {
            lights.changeBrightness(0.01);
        }
    }
}
