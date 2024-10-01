package com.team766.framework;

import static org.junit.jupiter.api.Assertions.*;

import com.team766.hal.TestClock;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.Test;

public class TimedPredicateTest {

    @Test
    public void testTimedPredicateTimedOut() {
        TestClock testClock = new TestClock(1710411240.0);
        ContextImpl.TimedPredicate predicate =
                new ContextImpl.TimedPredicate(testClock, () -> false, 1.766);
        assertFalse(predicate.getAsBoolean());
        testClock.tick(1.0);
        assertFalse(predicate.getAsBoolean());
        testClock.tick(0.766);
        assertTrue(predicate.getAsBoolean());
        assertFalse(predicate.succeeded());
    }

    @Test
    public void testTimedPredicateCondition() {
        TestClock testClock = new TestClock(1710411240.0);
        ContextImpl.TimedPredicate predicate =
                new ContextImpl.TimedPredicate(
                        testClock,
                        new BooleanSupplier() {
                            private int counter = 0;

                            public boolean getAsBoolean() {
                                return (counter++) >= 2;
                            }
                        },
                        1.766);
        assertFalse(predicate.getAsBoolean()); // 0
        testClock.tick(1.0);
        assertFalse(predicate.getAsBoolean()); // 1
        testClock.tick(0.766);
        assertTrue(predicate.getAsBoolean()); // 2 - success
        assertTrue(predicate.succeeded());
    }
}
