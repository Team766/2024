package com.team766.library;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class TransformingValueProviderTest {
    @Test
    public void testValue() {
        var sourceProvider = new SetValueProvider<Integer>(10);

        var xfProvider = new TransformingValueProvider<>(sourceProvider, v -> -v);
        assertTrue(xfProvider.hasValue());
        assertEquals(xfProvider.get(), -10);

        sourceProvider.set(20);
        assertTrue(xfProvider.hasValue());
        assertEquals(xfProvider.get(), -20);

        sourceProvider.clear();
        assertFalse(xfProvider.hasValue());

        var xfProvider2 = new TransformingValueProvider<>(sourceProvider, v -> -v);
        assertFalse(xfProvider2.hasValue());
    }

    @Test
    public void testObservation() {
        var sourceProvider = new SetValueProvider<Integer>(10);

        var xfProvider = new TransformingValueProvider<>(sourceProvider, v -> -v);

        final AtomicReference<Optional<Integer>> lastUpdate = new AtomicReference<>();
        Observer<Optional<Integer>> observer = lastUpdate::set;
        xfProvider.addObserver(observer);

        assertNull(lastUpdate.get());

        sourceProvider.set(20);
        assertEquals(lastUpdate.get(), Optional.of(-20));

        sourceProvider.clear();
        assertEquals(lastUpdate.get(), Optional.empty());

        xfProvider.removeObserver(observer);
        sourceProvider.set(30);
        assertEquals(lastUpdate.get(), Optional.empty());
    }
}
