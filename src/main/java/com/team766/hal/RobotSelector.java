package com.team766.hal;

import com.team766.config.ConfigFileReader;
import com.team766.library.ValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import com.team766.robot.example.ExampleConfigurator;

public final class RobotSelector {

    private static final String ROBOT_CONFIGURATOR_KEY = "robotConfigurator";
    private static final String DEFAULT_CONFIGURATOR = ExampleConfigurator.class.getName();

    private RobotSelector() {}

    public static RobotConfigurator createConfigurator() {
        ValueProvider<String> configuratorProvider =
                ConfigFileReader.instance.getString(ROBOT_CONFIGURATOR_KEY);
        String robotConfigurator =
                configuratorProvider.hasValue() ? configuratorProvider.get() : DEFAULT_CONFIGURATOR;

        try {
            Class<RobotConfigurator> clazz =
                    (Class<RobotConfigurator>) Class.forName(robotConfigurator);
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Logger.get(Category.FRAMEWORK)
                    .logData(
                            Severity.ERROR,
                            "Unable to create RobotConfigurator {0}.  Using default.",
                            robotConfigurator);
            LoggerExceptionUtils.logException(e);
            return new ExampleConfigurator();
        }
    }
}
