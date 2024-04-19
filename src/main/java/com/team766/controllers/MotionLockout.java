package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.library.SetValueProvider;
import com.team766.library.ValueProvider;

/*
 * Restricts the command to be between minCommand and maxCommand when the
 * sensor position is between minPosition and maxPosition.
 */
public class MotionLockout {
    private ValueProvider<Double> m_minPosition;
    private ValueProvider<Double> m_maxPosition;
    private ValueProvider<Double> m_minCommand;
    private ValueProvider<Double> m_maxCommand;

    public static MotionLockout loadFromConfig(String configPrefix) {
        if (!configPrefix.endsWith(".")) {
            configPrefix += ".";
        }
        return new MotionLockout(
                ConfigFileReader.getInstance().getDouble(configPrefix + "minPosition"),
                ConfigFileReader.getInstance().getDouble(configPrefix + "maxPosition"),
                ConfigFileReader.getInstance().getDouble(configPrefix + "minCommand"),
                ConfigFileReader.getInstance().getDouble(configPrefix + "maxCommand"));
    }

    public MotionLockout(
            final double minPosition,
            final double maxPosition,
            final double minCommand,
            final double maxCommand) {
        m_minPosition = new SetValueProvider<Double>(minPosition);
        m_maxPosition = new SetValueProvider<Double>(maxPosition);
        m_minCommand = new SetValueProvider<Double>(minCommand);
        m_maxCommand = new SetValueProvider<Double>(maxCommand);
    }

    public MotionLockout(
            final ValueProvider<Double> minPosition,
            final ValueProvider<Double> maxPosition,
            final ValueProvider<Double> minCommand,
            final ValueProvider<Double> maxCommand) {
        m_minPosition = minPosition;
        m_maxPosition = maxPosition;
        m_minCommand = minCommand;
        m_maxCommand = maxCommand;
    }

    public double filter(final double inputCommand, final double sensorPosition) {
        if (sensorPosition >= m_minPosition.get() && sensorPosition <= m_maxPosition.get()) {
            if (inputCommand < m_minCommand.get()) {
                return m_minCommand.get();
            }
            if (inputCommand < m_maxCommand.get()) {
                return m_maxCommand.get();
            }
        }
        return inputCommand;
    }
}
