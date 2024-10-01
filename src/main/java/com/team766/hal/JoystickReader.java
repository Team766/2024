package com.team766.hal;

public interface JoystickReader {
    /**
     * Get the value of the axis.
     *
     * If a deadzone has been set for this axis, the returned value will be 0 if the value would be
     * smaller than the size of the deadzone.
     *
     * @param axis The axis to read, starting at 0.
     * @return The value of the axis.
     */
    double getAxis(int axis);

    /**
     * Get whether the axis has an absolute value greater than or equal to the deadzone.
     *
     * @param axis The axis to read, starting at 0.
     * @return True if the axis value is larger than or equal to the deadzone, else false.
     */
    boolean isAxisMoved(int axis);

    /**
     * Set the size of the deadzone for the given axis.
     *
     * @param axis The axis to read, starting at 0.
     * @param deadzone The size of the deadzone. 0 disables the deadzone.
     */
    void setAxisDeadzone(int axis, double deadzone);

    /**
     * Set the size of the deadzone for all axes (overriding any previous calls to setAxisDeadzone).
     *
     * Deadzones for individual axes can be overridden by calling setAxisDeadzone.
     *
     * @param deadzone The size of the deadzone. 0 disables the deadzone.
     */
    void setAllAxisDeadzone(double deadzone);

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

    /**
     * Get the value of the POV
     *
     * @return the value of the POV
     */
    int getPOV();
}
