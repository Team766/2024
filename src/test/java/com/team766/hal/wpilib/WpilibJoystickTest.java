package com.team766.hal.wpilib;

import com.team766.hal.JoystickAbstractTest;
import edu.wpi.first.wpilibj.simulation.JoystickSim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

// TODO: AdvantageKit breaks this test. Try re-enabling this after upgrade to 2025.
@Disabled
public class WpilibJoystickTest extends JoystickAbstractTest {
    private JoystickSim driver;

    @BeforeEach
    public void setUp() {
        joystick = new Joystick(0);
        driver = new JoystickSim(0);
    }

    @Override
    protected void setAxis(int axis, double value) {
        driver.setRawAxis(axis, value);
        updateDriverStationData();
    }
}
