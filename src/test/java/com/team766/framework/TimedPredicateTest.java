package com.team766.framework;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.Test;

public class TimedPredicateTest {

    public static class TestClock extends Clock {
        private Instant instant;

        public TestClock(Instant instant) {
            this.instant = instant;
        }

        public void tick(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public Instant instant() {
            return instant;
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zoneId) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void testTimedPredicateTimedOut() {
        TestClock testClock = new TestClock(Instant.ofEpochMilli(1710411240000L));
        Context.TimedPredicate predicate =
                new Context.TimedPredicate(testClock, () -> false, 1.766);
        assertFalse(predicate.getAsBoolean());
        testClock.tick(Duration.ofSeconds(1));
        assertFalse(predicate.getAsBoolean());
        testClock.tick(Duration.ofMillis(766));
        assertTrue(predicate.getAsBoolean());
        assertFalse(predicate.succeeded());
    }

    @Test
    public void testTimedPredicateCondition() {
        TestClock testClock = new TestClock(Instant.ofEpochMilli(1710411240000L));
        Context.TimedPredicate predicate =
                new Context.TimedPredicate(
                        testClock,
                        new BooleanSupplier() {
                            private int counter = 0;

                            public boolean getAsBoolean() {
                                return (counter++) >= 2;
                            }
                        },
                        1.766);
        assertFalse(predicate.getAsBoolean()); // 0
        testClock.tick(Duration.ofSeconds(1));
        assertFalse(predicate.getAsBoolean()); // 1
        testClock.tick(Duration.ofMillis(766));
        assertTrue(predicate.getAsBoolean()); // 2 - success
        assertTrue(predicate.succeeded());
    }
}
