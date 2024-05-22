package com.team766.hal;

import com.team766.framework.conditions.Condition;
import com.team766.framework.conditions.ConditionState;
import com.team766.framework.conditions.RulesMixin;
import com.team766.framework.conditions.RulesMixin.ValueCondition;
import com.team766.library.ArrayUtils;
import com.team766.library.Lazy;

public interface JoystickReader {
    public final class Companion {
        private final Lazy<Condition>[] buttonConditions;
        private final Lazy<ValueCondition<Double>>[] axisConditions;
        private final Lazy<ValueCondition<Double>> disconnectedAxis;
        private final Lazy<ValueCondition<Integer>> povCondition;

        public Companion(JoystickReader j, RulesMixin oi) {
            buttonConditions = ArrayUtils.initializeArray(
                    j.getMaxButtonCount(),
                    button -> new Lazy<>(
                            () -> oi.new DeclaredCondition(() -> j.getButton(button + 1))));
            axisConditions = ArrayUtils.initializeArray(
                    j.getMaxAxisCount(),
                    axis -> new Lazy<>(() -> oi.new ValueCondition<>(() -> j.getAxis(axis))));
            disconnectedAxis = new Lazy<>(() -> oi.new ValueCondition<>(() -> 0.0));
            povCondition = new Lazy<>(() -> oi.new ValueCondition<>(() -> j.getPOV()));
        }
    }

    Companion getCompanion();

    /**
     * Get the value of the axis.
     *
     * @param axis The axis to read, starting at 0.
     * @return The value of the axis.
     */
    double getAxis(int axis);

    int getMaxAxisCount();

    default ValueCondition<Double> axis(int axis) {
        final var c = getCompanion();
        if (axis < c.axisConditions.length) {
            return c.axisConditions[axis].get();
        } else {
            return c.disconnectedAxis.get();
        }
    }

    /**
     * Get the state of a button (starting at button 1)
     *
     * @param button The button number to be read (starting at 1).
     * @return The state of the button.
     */
    boolean getButton(int button);

    /**
     * Get the state of a button (starting at button 1)
     *
     * @param button The button number to be read (starting at 1).
     * @return The state of the button.
     */
    default ConditionState button(int button) {
        final var c = getCompanion();
        button -= 1;
        if (button < c.buttonConditions.length) {
            return c.buttonConditions[button].get().getState();
        } else {
            return ConditionState.IsNotTriggering;
        }
    }

    int getMaxButtonCount();

    /**
     * Get the angle in degrees of the POV.
     *
     * <p>The POV angles start at 0 in the up direction, and increase clockwise (e.g. right is 90,
     * upper-left is 315).
     *
     * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
     */
    int getPOV();

    default ValueCondition<Integer> getPOVCondition() {
        return getCompanion().povCondition.get();
    }
}
