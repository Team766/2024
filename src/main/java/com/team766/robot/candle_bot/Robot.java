package com.team766.robot.candle_bot;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;
import com.team766.hal.RobotConfigurator;
import com.team766.robot.candle_bot.mechanisms.candle;
import com.team766.robot.example.mechanisms.*;

public class Robot implements RobotConfigurator {
    public static candle candle;
    // Declare mechanisms (as static fields) here

    @Override
    public void initializeMechanisms() {
        candle = new candle();
        // Initialize mechanisms here
    }

    @Override
    public Procedure createOI() {
        return new OI();
    }

    @Override
    public AutonomousMode[] getAutonomousModes() {
        return AutonomousModes.AUTONOMOUS_MODES;
    }
}
