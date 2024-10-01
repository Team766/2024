package com.team766.framework3;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.Test;

public class ConditionsTest {
    private static class ValueProxy implements BooleanSupplier {
        boolean value = false;

        public boolean getAsBoolean() {
            return value;
        }
    }

    @Test
    public void testToggle() {
        var v = new ValueProxy();

        var t = new Conditions.Toggle(v);

        assertFalse(t.getAsBoolean());
        assertFalse(t.getAsBoolean());

        v.value = true;

        assertTrue(t.getAsBoolean());
        assertTrue(t.getAsBoolean());

        v.value = false;

        assertTrue(t.getAsBoolean());
        assertTrue(t.getAsBoolean());

        v.value = true;

        assertFalse(t.getAsBoolean());
        assertFalse(t.getAsBoolean());

        v.value = false;

        assertFalse(t.getAsBoolean());
        assertFalse(t.getAsBoolean());
    }
}
