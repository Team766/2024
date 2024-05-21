package com.team766.hal;

import com.team766.framework.AutonomousMode;
import com.team766.framework.LightsBase;
import com.team766.framework.OIBase;
import com.team766.framework.resources.ResourceManager;
import edu.wpi.first.wpilibj2.command.Subsystem;

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
    void initializeSubsystems();

    default void addSubsystem(Subsystem subsystem) {
        ResourceManager.addSubsystem(subsystem);
    }

    /**
     * Creates the Operator Interface (OI) for this robot.
     */
    OIBase createOI();

    LightsBase createLights();

    /**
     * Returns an array of {@link AutonomousMode}s available for this robot.
     */
    AutonomousMode[] getAutonomousModes();
}
