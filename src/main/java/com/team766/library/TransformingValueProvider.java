package com.team766.library;

import java.util.Optional;
import java.util.function.Function;

/**
 * This class implements a ValueProvider whose value (and the presence of its value) is based
 * another ValueProvider, but the value from the underlying ValueProvided is transformed using the
 * given transform function.
 */
public final class TransformingValueProvider<E, T> extends AbstractObservable<Optional<E>>
        implements ValueProvider<E> {
    private Optional<E> m_cachedValue;

    /**
     * @param source The underlying ValueProvider which will provide the values passed to the
     *     transform function.
     * @param transform The transform function applied to the values.
     */
    public TransformingValueProvider(ValueProvider<T> source, Function<T, E> transform) {
        source.addObserver(
                optValue -> {
                    m_cachedValue = optValue.map(value -> transform.apply(value));
                    notifyObservers(m_cachedValue);
                });
        m_cachedValue =
                source.hasValue() ? Optional.of(transform.apply(source.get())) : Optional.empty();
    }

    @Override
    public E get() {
        return m_cachedValue.get();
    }

    @Override
    public boolean hasValue() {
        return m_cachedValue.isPresent();
    }
}
