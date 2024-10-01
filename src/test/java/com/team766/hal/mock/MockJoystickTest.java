package com.team766.hal.mock;

import com.team766.hal.JoystickAbstractTest;
import org.junit.jupiter.api.BeforeEach;

public class MockJoystickTest extends JoystickAbstractTest {
    private MockJoystick driver;

    @BeforeEach
    public void setUp() {
        joystick = driver = new MockJoystick();
    }

    @Override
    protected void setAxis(int axis, double value) {
        driver.setAxisValue(axis, value);
    }
}
