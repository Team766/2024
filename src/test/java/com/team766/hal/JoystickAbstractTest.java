package com.team766.hal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team766.TestCase3;
import org.junit.jupiter.api.Test;

public abstract class JoystickAbstractTest extends TestCase3 {
    protected JoystickReader joystick;

    protected abstract void setAxis(int axis, double value);

    @Test
    public void testDeadzone() {
        // Deadzone should start at 0.0, so the condition should be true even if the value is 0.
        setAxis(0, 0.0);
        setAxis(1, 0.0);
        assertEquals(0.0, joystick.getAxis(0));
        assertEquals(0.0, joystick.getAxis(1));
        assertTrue(joystick.isAxisMoved(0));
        assertTrue(joystick.isAxisMoved(1));

        // Same result if the deadzone is explicitly set to 0.0.
        joystick.setAllAxisDeadzone(0.0);
        assertTrue(joystick.isAxisMoved(0));
        assertTrue(joystick.isAxisMoved(1));

        // Test with the deadzone larger than the axis values.
        joystick.setAllAxisDeadzone(0.6);
        assertFalse(joystick.isAxisMoved(0));
        assertFalse(joystick.isAxisMoved(1));

        // Calling setAxisDeadzone after setAllAxisDeadzone should set the deadzone for that axis
        // but maintain the deadzone for all other axes.
        setAxis(0, 0.5);
        setAxis(1, 0.3);
        joystick.setAxisDeadzone(1, 0.2);
        assertEquals(0.0, joystick.getAxis(0));
        assertEquals(0.3, joystick.getAxis(1));
        assertFalse(joystick.isAxisMoved(0));
        assertTrue(joystick.isAxisMoved(1));

        // Calling setAllAxisDeadzone should override previously-set per-axis deadzones.
        joystick.setAllAxisDeadzone(0.5);
        assertEquals(0.5, joystick.getAxis(0));
        assertEquals(0.0, joystick.getAxis(1));
        assertTrue(joystick.isAxisMoved(0));
        assertFalse(joystick.isAxisMoved(1));
    }
}
