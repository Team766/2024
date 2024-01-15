package com.team766.hal;

import com.team766.framework.AutonomousMode;
import com.team766.framework.Procedure;

// TODO: better name?
public interface RobotConfigurator {
    void initializeMechanisms();

    Procedure createOI();

    AutonomousMode[] getAutonomousModes();
}
