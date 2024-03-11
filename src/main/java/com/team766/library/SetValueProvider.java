package com.team766.library;

import java.util.Optional;

public class SetValueProvider<E> extends AbstractObservable<Optional<E>>
        implements SettableValueProvider<E> {
    private E m_value;
    private boolean m_hasValue;

    public SetValueProvider() {
        m_value = null;
        m_hasValue = false;
    }

    public SetValueProvider(final E value) {
        m_value = value;
        m_hasValue = true;
    }

    @Override
    public E get() {
        return m_value;
    }

    @Override
    public boolean hasValue() {
        return m_hasValue;
    }

    public void set(final E value) {
        m_value = value;
        m_hasValue = true;
        notifyObservers(Optional.of(m_value));
    }

    public void clear() {
        m_value = null;
        m_hasValue = false;
        notifyObservers(Optional.empty());
    }
}
