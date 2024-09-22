package com.team766.config;

import java.util.Arrays;
import java.util.stream.Collectors;

class DoubleConfigValue extends AbstractConfigValue<Double> {
    protected DoubleConfigValue(final String key) {
        super(key);
    }

    @Override
    public Double parseJsonValue(final Object configValue) {
        return ((Number) configValue).doubleValue();
    }
}

class IntegerConfigValue extends AbstractConfigValue<Integer> {
    protected IntegerConfigValue(final String key) {
        super(key);
    }

    @Override
    public Integer parseJsonValue(final Object configValue) {
        return ((Number) configValue).intValue();
    }
}

class DoubleConfigMultiValue extends AbstractConfigMultiValue<Double> {
    protected DoubleConfigMultiValue(final String key) {
        super(key, Double.class);
    }

    @Override
    public Double parseJsonElement(final Object configElement) {
        return ((Number) configElement).doubleValue();
    }
}

class IntegerConfigMultiValue extends AbstractConfigMultiValue<Integer> {
    protected IntegerConfigMultiValue(final String key) {
        super(key, Integer.class);
    }

    @Override
    public Integer parseJsonElement(final Object configElement) {
        return ((Number) configElement).intValue();
    }
}

class BooleanConfigValue extends AbstractConfigValue<Boolean> {
    protected BooleanConfigValue(final String key) {
        super(key);
    }

    @Override
    public Boolean parseJsonValue(final Object configValue) {
        return (Boolean) configValue;
    }
}

class StringConfigValue extends AbstractConfigValue<String> {
    protected StringConfigValue(final String key) {
        super(key);
    }

    @Override
    public String parseJsonValue(final Object configValue) {
        return (String) configValue;
    }
}

class EnumConfigValue<E extends Enum<E>> extends AbstractConfigValue<E> {
    Class<E> enumClass;

    protected EnumConfigValue(final Class<E> enumClass_, final String key) {
        super(key);
        this.enumClass = enumClass_;
    }

    @Override
    public E parseJsonValue(final Object configValue) {
        String enumName = (String) configValue;
        for (E each : enumClass.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(enumName) == 0) {
                return each;
            }
        }
        throw new IllegalArgumentException(
                "Unrecognized enum value: "
                        + enumName
                        + "; values are "
                        + Arrays.stream(enumClass.getEnumConstants())
                                .map(e -> e.name())
                                .collect(Collectors.joining(", ")));
    }
}
