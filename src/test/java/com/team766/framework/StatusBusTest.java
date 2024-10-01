package com.team766.framework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.team766.TestCase;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class StatusBusTest extends TestCase {

    public record MyStatus(int value) implements Status {}

    public record OtherStatus(int value) implements Status {}

    @Test
    public void testGetStatusEntry() {
        assertEquals(Optional.empty(), StatusBus.getStatusEntry(MyStatus.class));
        StatusBus.publishStatus(new OtherStatus(0));
        assertEquals(Optional.empty(), StatusBus.getStatusEntry(MyStatus.class));

        testClock.setTime(1234500000);
        StatusBus.publishStatus(new MyStatus(0));

        testClock.setTime(1234500123);
        var maybeEntry = StatusBus.getStatusEntry(MyStatus.class);
        var entry = maybeEntry.orElseThrow();
        assertEquals(new MyStatus(0), entry.status());
        assertEquals(1234500000, entry.timestamp());
        assertEquals(123, entry.age());
    }

    @Test
    public void testPublishStatus() {
        testClock.setTime(1234500000);
        var publishEntry = StatusBus.publishStatus(new MyStatus(42));
        assertEquals(new MyStatus(42), publishEntry.status());
        assertEquals(1234500000, publishEntry.timestamp());

        testClock.setTime(1234500123);
        assertEquals(123, publishEntry.age());

        var maybeEntry = StatusBus.getStatusEntry(MyStatus.class);
        var entry = maybeEntry.orElseThrow();
        assertEquals(new MyStatus(42), entry.status());
        assertEquals(1234500000, entry.timestamp());
        assertEquals(123, entry.age());

        // Test that publishing another status overwrites the first status.

        testClock.setTime(1234501000);
        publishEntry = StatusBus.publishStatus(new MyStatus(66));
        assertEquals(new MyStatus(66), publishEntry.status());
        assertEquals(1234501000, publishEntry.timestamp());

        testClock.setTime(1234501012);
        assertEquals(12, publishEntry.age());

        entry = StatusBus.getStatusEntry(MyStatus.class).orElseThrow();
        assertEquals(new MyStatus(66), entry.status());
        assertEquals(1234501000, entry.timestamp());
        assertEquals(12, entry.age());
    }

    @Test
    public void testClear() {
        StatusBus.publishStatus(new MyStatus(0));
        assertTrue(StatusBus.getStatusEntry(MyStatus.class).isPresent());
        StatusBus.clear();
        assertFalse(StatusBus.getStatusEntry(MyStatus.class).isPresent());
    }

    @Test
    public void testGetStatus() {
        assertEquals(Optional.empty(), StatusBus.getStatus(MyStatus.class));
        StatusBus.publishStatus(new OtherStatus(0));
        assertEquals(Optional.empty(), StatusBus.getStatus(MyStatus.class));
        StatusBus.publishStatus(new MyStatus(0));
        assertEquals(Optional.of(new MyStatus(0)), StatusBus.getStatus(MyStatus.class));
    }

    @Test
    public void testGetStatusOrThrow() {
        assertThrows(
                NoSuchElementException.class, () -> StatusBus.getStatusOrThrow(MyStatus.class));
        StatusBus.publishStatus(new OtherStatus(0));
        assertThrows(
                NoSuchElementException.class, () -> StatusBus.getStatusOrThrow(MyStatus.class));
        StatusBus.publishStatus(new MyStatus(0));
        assertEquals(new MyStatus(0), StatusBus.getStatusOrThrow(MyStatus.class));
    }

    @Test
    public void testGetStatusValue() {
        assertEquals(Optional.empty(), StatusBus.getStatusValue(MyStatus.class, s -> s.value()));
        StatusBus.publishStatus(new OtherStatus(0));
        assertEquals(Optional.empty(), StatusBus.getStatusValue(MyStatus.class, s -> s.value()));
        StatusBus.publishStatus(new MyStatus(42));
        assertEquals(Optional.of(42), StatusBus.getStatusValue(MyStatus.class, s -> s.value()));
    }
}
