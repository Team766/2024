package com.team766.library;

import java.util.Optional;

/**
 * This class aids an Observer which may wish to subscribe a succession of different ValueProviders.
 *
 * This class will handle registering and de-registering the Observer as setValueProvider is called
 * with different ValueProviders, to ensure that the Observer receives change notifications only
 * from the current ValueProvider. Additionally, the Observer will be notifed when changing to a
 * different ValueProvider, to indicate that the current value (as considered across the multiple
 * ValueProviders) may have changed. Note that this notification also happens when setting the
 * initial ValueProvider.
 */
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

    /**
     * Construct the ObserveValue with an initial ValueProvider to register with.
     * This is equivalent to constructing this ObserveValue and then immediately calling
     * setValueProvider.
     */
    public ObserveValue(ValueProvider<E> provider, Observer<Optional<E>> observer) {
        m_observer = observer;
        setValueProvider(provider);
    }

    /**
     * Get the ValueProvider that was most recently passed to setValueProvider (or the constructor
     * of this ObserveValue). Returns null if no ValueProvider has been set yet.
     */
    public ValueProvider<E> getValueProvider() {
        return m_provider;
    }

    /**
     * Change the Observer so that it is registered to the given ValueProvider (instead of the
     * previous one). Also notifies the Observer with the value of the new ValueProvider.
     */
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
