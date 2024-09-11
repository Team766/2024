package com.team766.hal;

public interface GenericRobotMain {
    void robotInit();

    void disabledInit();

    void disabledPeriodic();

    void resetAutonomousMode(final String reason);

    void autonomousInit();

    void autonomousPeriodic();

    void teleopInit();

    void teleopPeriodic();
}
