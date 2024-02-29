package com.team766.config;

import com.team766.library.AbstractObservable;
import com.team766.library.SettableValueProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public abstract class AbstractConfigValue<E> extends AbstractObservable<Optional<E>>
        implements SettableValueProvider<E> {
    protected String m_key;
    private E m_cachedValue;
    private boolean m_cachedHasValue;

    private static ArrayList<AbstractConfigValue<?>> c_accessedValues =
            new ArrayList<AbstractConfigValue<?>>();

    static Collection<AbstractConfigValue<?>> accessedValues() {
        return Collections.unmodifiableCollection(c_accessedValues);
    }

    static void resetStatics() {
        c_accessedValues.clear();
    }

    protected AbstractConfigValue(final String key) {
        m_key = key;
        c_accessedValues.add(this);
        // Querying for this config setting's key will add a placeholder entry
        // in the config file if this setting does not already exist there.
        ConfigFileReader.instance.getRawValue(m_key);
        update();
    }

    void update() {
        var rawValue = ConfigFileReader.instance.getRawValue(m_key);
        m_cachedHasValue = rawValue != null;
        if (m_cachedHasValue) {
            try {
                m_cachedValue = parseJsonValue(rawValue);
            } catch (Exception ex) {
                Logger.get(Category.CONFIGURATION)
                        .logRaw(
                                Severity.ERROR,
                                "Failed to parse "
                                        + m_key
                                        + " from the config file: "
                                        + LoggerExceptionUtils.exceptionToString(ex));
                m_cachedValue = null;
                m_cachedHasValue = false;
            }
        }
        notifyObservers(m_cachedHasValue ? Optional.of(m_cachedValue) : Optional.empty());
    }

    public String getKey() {
        return m_key;
    }

    @Override
    public boolean hasValue() {
        return m_cachedHasValue;
    }

    @Override
    public E get() {
        if (!m_cachedHasValue) {
            throw new IllegalArgumentException(m_key + " not found in the config file");
        }
        return m_cachedValue;
    }

    public void set(final E value) {
        ConfigFileReader.instance.setValue(m_key, value);
        notifyObservers(Optional.of(value));
    }

    public void clear() {
        ConfigFileReader.instance.setValue(m_key, null);
        notifyObservers(Optional.empty());
    }

    protected abstract E parseJsonValue(Object configValue);

    @Override
    public String toString() {
        if (!m_cachedHasValue) {
            return "<unset>";
        }
        if (m_cachedValue == null) {
            return "<null>";
        }
        return m_cachedValue.toString();
    }
}
