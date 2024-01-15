package com.team766.hal;

public final class RobotSelector {
    private RobotSelector() {}

    public static RobotConfigurator createConfigurator(String configurator) throws Exception {
        Class<RobotConfigurator> clazz = (Class<RobotConfigurator>) Class.forName(configurator);
        return clazz.getDeclaredConstructor().newInstance();
    }
}
