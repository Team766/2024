package com.team766.hal;

import com.team766.framework.Condition;

public interface JoystickReader {
    /**
     * Get the value of the axis.
     *
     * @param axis The axis to read, starting at 0.
     * @return The value of the axis.
     */
    double getAxis(int axis);

    /**
     * Get the button value (starting at button 1)
     *
     * The appropriate button is returned as a boolean value.
     *
     * @param button The button number to be read (starting at 1).
     * @return The state of the button.
     */
    boolean getButton(int button);

    /**
     * Whether the button was pressed since the last check. Button indexes begin at 1.
     *
     * @param button The button index, beginning at 1.
     * @return Whether the button was pressed since the last check.
     */
    boolean getButtonPressed(int button);

    /**
     * Whether the button was released since the last check. Button indexes begin at 1.
     *
     * @param button The button index, beginning at 1.
     * @return Whether the button was released since the last check.
     */
    boolean getButtonReleased(int button);

    class ButtonCondition implements Condition {
        private final JoystickReader joystick;
        private final int button;

        public ButtonCondition(JoystickReader joystick, int button) {
            this.joystick = joystick;
            this.button = button;
        }

        public boolean isTriggering() {
            return joystick.getButton(button);
        }

        public boolean isNewlyTriggering() {
            return joystick.getButtonPressed(button);
        }

        public boolean isFinishedTriggering() {
            return joystick.getButtonReleased(button);
        }
    }

    default Condition getButtonCondition(int button) {
        return new ButtonCondition(this, button);
    }

    /**
     * Get the value of the POV
     *
     * @return the value of the POV
     */
    int getPOV();
}
