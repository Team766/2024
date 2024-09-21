package com.team766.hal;

import com.team766.framework3.AutonomousMode;
import com.team766.framework3.RuleEngine;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;

/**
 * Provides Robot-specific components: initializes {@link Mechanism}s, creates the Operator Interface (OI),
 * and returns the {@link AutonomousMode}s.
 *
 * @see RobotSelector
 */
public interface RobotConfigurator3 extends RobotConfiguratorBase {

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

    @Override
    default GenericRobotMainBase createRobotMain() {
        Logger.get(Category.FRAMEWORK).logRaw(Severity.INFO, "Instantiating GenericRobotMain3");
        return new GenericRobotMain3(this);
    }
}
