package com.team766.robot.smores;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.robot.smores.constants.InputConstants;
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
        if (macropad.getButtonPressed(InputConstants.RAINBOW_ANIMATION)) {
            lights.rainbow();
        } else if (macropad.getButtonPressed(InputConstants.RANDOM_COLOR)) {
            lights.randColor();
        } else if (macropad.getButtonPressed(InputConstants.FADE)) {
            lights.fade(255, 0, 0);
        } else if (macropad.getButtonPressed(InputConstants.CLEAR)) {
            lights.clear();
        }

        if (macropad.getButtonPressed(InputConstants.SET_WHITE_OR_CLEAR)) {
            lights.setColor(255, 255, 255);
        } else if (macropad.getButtonReleased(InputConstants.SET_WHITE_OR_CLEAR)) {
            lights.clear();
        }

        // Brightness adjusting
        if (macropad.getButton(InputConstants.DECREASE_BRIGHTNESS)) {
            lights.changeBrightness(-0.01);
        }
        if (macropad.getButton(InputConstants.INCREASE_BRIGHTNESS)) {
            lights.changeBrightness(0.01);
        }
    }
}
