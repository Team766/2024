package com.team766.controllers;

import com.team766.config.ConfigFileReader;
import com.team766.library.SetValueProvider;
import com.team766.library.ValueProvider;

/*
 * Limits the given value to be between between configured min and max values
 */
public class RangeBound {
    private ValueProvider<Double> m_min;
    private ValueProvider<Double> m_max;

    public static RangeBound loadFromConfig(String configPrefix) {
        if (!configPrefix.endsWith(".")) {
            configPrefix += ".";
        }
        return new RangeBound(
                ConfigFileReader.getInstance().getDouble(configPrefix + "min"),
                ConfigFileReader.getInstance().getDouble(configPrefix + "max"));
    }

    public RangeBound(final double min, final double max) {
        m_min = new SetValueProvider<Double>(min);
        m_max = new SetValueProvider<Double>(max);
    }

    public RangeBound(final ValueProvider<Double> min, final ValueProvider<Double> max) {
        m_min = min;
        m_max = max;
    }

    public double filter(final double input) {
        return Math.min(Math.max(input, m_min.get()), m_max.get());
    }
}
