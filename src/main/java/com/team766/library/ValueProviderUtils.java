package com.team766.library;

import java.util.Optional;
import java.util.function.Function;

public class ValueProviderUtils {
    public static <E, T> ValueProvider<E> transform(
            final ValueProvider<T> source, final Function<T, E> transform) {
        var transformingProvider =
                new AbstractValueProvider<E>() {
                    private Optional<E> m_cachedValue =
                            source.hasValue()
                                    ? Optional.of(transform.apply(source.get()))
                                    : Optional.empty();

                    void onValueUpdated(Optional<T> optValue) {
                        m_cachedValue = optValue.map(value -> transform.apply(value));
                        notifyObservers(m_cachedValue);
                    }

                    @Override
                    public E get() {
                        return m_cachedValue.get();
                    }

                    @Override
                    public boolean hasValue() {
                        return m_cachedValue.isPresent();
                    }
                };
        source.addObserver(transformingProvider::onValueUpdated);
        return transformingProvider;
    }
}
