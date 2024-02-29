package com.team766.library;

import java.util.Optional;

public class ObserveValue<E> {
    private final Observer<Optional<E>> m_observer;
    private ValueProvider<E> m_provider = null;

    public static <E> Observer<Optional<E>> whenPresent(Observer<E> delegate) {
        return (optValue) -> {
            if (optValue.isPresent()) delegate.onValueUpdated(optValue.get());
        };
    }

    public ObserveValue(Observer<Optional<E>> observer) {
        m_observer = observer;
    }

    public ObserveValue(ValueProvider<E> provider, Observer<Optional<E>> observer) {
        m_observer = observer;
        setValueProvider(provider);
    }

    public ValueProvider<E> getValueProvider() {
        return m_provider;
    }

    public void setValueProvider(ValueProvider<E> provider) {
        if (m_provider != null) {
            m_provider.removeObserver(m_observer);
        }
        m_provider = provider;
        if (m_provider != null) {
            m_provider.addObserver(m_observer);
        }
        m_observer.onValueUpdated(
                m_provider != null && m_provider.hasValue()
                        ? Optional.of(m_provider.get())
                        : Optional.empty());
    }
}
