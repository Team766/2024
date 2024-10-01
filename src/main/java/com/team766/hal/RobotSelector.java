package com.team766.hal;

import com.team766.config.ConfigFileReader;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import com.team766.robot.example.Robot;

/**
 * Utility class that reads the name of a {@link RobotConfigurator} from the config file, under the key
 * 'robotConfigurator', and creates an instance of that configurator.
 */
public final class RobotSelector {

    private static final String ROBOT_CONFIGURATOR_KEY = "robotConfigurator";
    private static final String DEFAULT_CONFIGURATOR = Robot.class.getName();

    private RobotSelector() {} // utility class

    /**
     * Creates a configurator specified by the 'robotConfigurator' key in the config file.
     * If unable to create this configurator, backs off to the example one.
     */
    public static RobotConfigurator createConfigurator() {
        ValueProvider<String> configuratorProvider =
                ConfigFileReader.instance.getString(ROBOT_CONFIGURATOR_KEY);
        String robotConfigurator =
                configuratorProvider.hasValue() ? configuratorProvider.get() : DEFAULT_CONFIGURATOR;

        Logger.get(Category.CONFIGURATION)
                .logData(
                        Severity.INFO,
                        "Using robot configuration from %s",
                        robotConfigurator.toString());

        try {
            @SuppressWarnings("unchecked")
            Class<RobotConfigurator> clazz =
                    (Class<RobotConfigurator>) Class.forName(robotConfigurator);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Logger.get(Category.CONFIGURATION)
                    .logData(
                            Severity.ERROR,
                            "Unable to create RobotConfigurator %s.  Using default (%s).",
                            robotConfigurator,
                            DEFAULT_CONFIGURATOR);
            LoggerExceptionUtils.logException(e);
            return new Robot();
        }
    }
}
