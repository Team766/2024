package com.team766.hal;

public interface JoystickReader {
    /**
     * Get the value of the axis.
     *
     * @param axis The axis to read, starting at 0.
     * @return The value of the axis.
     */
    double getAxis(int axis);

    /**
     * Get the state of a button (starting at button 1)
     *
     * @param button The button number to be read (starting at 1).
     * @return The state of the button.
     */
    boolean getButton(int button);

    /**
     * Get the angle in degrees of the POV.
     *
     * <p>The POV angles start at 0 in the up direction, and increase clockwise (e.g. right is 90,
     * upper-left is 315).
     *
     * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
     */
    int getPOV();
}
