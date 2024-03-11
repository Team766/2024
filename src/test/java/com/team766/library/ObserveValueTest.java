package com.team766.library;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class ObserveValueTest {
    @Test
    public void testNotificationFromValueProvider() {
        var provider = new SetValueProvider<Integer>(10);

        final AtomicReference<Optional<Integer>> lastUpdate = new AtomicReference<>();
        @SuppressWarnings("unused")
        ObserveValue<Integer> observe = new ObserveValue<>(provider, lastUpdate::set);

        // Test notification from initial value provider.
        assertEquals(lastUpdate.get(), Optional.of(10));

        // Test notification from setting a new value in the provider.
        provider.set(20);
        assertEquals(lastUpdate.get(), Optional.of(20));

        // Test notification from clearing the value in the provider.
        provider.clear();
        assertEquals(lastUpdate.get(), Optional.empty());

        // Test notification from re-setting the value in the provider.
        provider.set(30);
        assertEquals(lastUpdate.get(), Optional.of(30));
    }

    @Test
    public void testNotificationFromChangingValueProviders() {
        final AtomicReference<Optional<Integer>> lastUpdate = new AtomicReference<>();
        ObserveValue<Integer> observe = new ObserveValue<>(lastUpdate::set);

        assertNull(lastUpdate.get());

        observe.setValueProvider(new SetValueProvider<Integer>(10));
        assertEquals(lastUpdate.get(), Optional.of(10));

        observe.setValueProvider(new SetValueProvider<Integer>());
        assertEquals(lastUpdate.get(), Optional.empty());

        observe.setValueProvider(new SetValueProvider<Integer>(20));
        assertEquals(lastUpdate.get(), Optional.of(20));
    }
}
