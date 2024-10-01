package com.team766.hal;

import com.team766.framework.AutonomousMode;
import com.team766.framework.RuleEngine;

/**
 * Provides Robot-specific components: initializes {@link Mechanism}s, creates the Operator Interface (OI),
 * and returns the {@link AutonomousMode}s.
 *
 * @see RobotSelector
 */
public interface RobotConfigurator {

    /**
     * Initializes the {@link Mechanism}s for this robot.
     *
     * Will only be called once by the framework.
     */
    void initializeMechanisms();

    /**
     * Creates the Operator Interface (OI) RuleEngine for this robot.
     */
    RuleEngine createOI();

    /**
     * Creates the Lights RuleEngine for this robot.
     */
    RuleEngine createLights();

    /**
     * Returns an array of {@link AutonomousMode}s available for this robot.
     */
    AutonomousMode[] getAutonomousModes();
}
