package com.team766.hal;

import com.team766.framework.conditions.ConditionState;
import com.team766.framework.conditions.RulesMixin.ValueCondition;

public interface JoystickReader {
    /**
     * Get the value of the axis.
     *
     * @param axis The axis to read, starting at 0.
     * @return The value of the axis.
     */
    double getAxis(int axis);

    ValueCondition<Double> getAxisCondition(int axis);

    /**
     * Get the state of a button (starting at button 1)
     *
     * @param button The button number to be read (starting at 1).
     * @return The state of the button.
     */
    ConditionState getButton(int button);

    /**
     * Get the value of the POV
     *
     * @return the value of the POV
     */
    int getPOV();

    ValueCondition<Integer> getPOVCondition();
}
