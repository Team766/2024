package com.team766.framework3;

import java.util.function.BooleanSupplier;

/**
 * Pre-canned Conditions used frqeuently in robot programming.
 */
public class Conditions {

    /**
     * Predicate that checks whether or not the latest {@link Status} passes the
     * check provided by {@link Checker}.
     */
    public static class StatusCheck<S extends Status> implements BooleanSupplier {

        /** Functional interface for checking whether or not a {@link Status} passes desired criteria. */
        @FunctionalInterface
        public interface Checker<S> {
            boolean check(S status);
        }

        private final Class<S> clazz;
        private final Checker<S> checker;

        public StatusCheck(Class<S> clazz, Checker<S> checker) {
            this.clazz = clazz;
            this.checker = checker;
        }

        public boolean getAsBoolean() {
            S status = StatusBus.getInstance().getStatus(clazz);
            return checker.check(status);
        }
    }

    /**
     * Predicate that checks if the provided {@link Request} has been fulfilled by checking
     * the latest {@link Status}.
     */
    public static class AwaitRequest<S extends Status> extends StatusCheck<S> {

        public AwaitRequest(Class<S> clazz, Request<S> request) {
            super(clazz, request::isDone);
        }
    }

    /**
     * This predicate toggles its value (false -> true, or true -> false) whenever the provided
     * predicate changes from false to true (rising edge). Otherwise, it retains its previous value.
     *
     * This is useful when you want a joystick button to switch between two different modes whenever
     * it is pushed (pass `() -> joystick.getButton()` as the constructor argument).
     */
    public static final class Toggle implements BooleanSupplier {
        private final BooleanSupplier predicate;
        private boolean predicatePrevious = false;
        private boolean toggleValue = false;

        public Toggle(BooleanSupplier predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean getAsBoolean() {
            final var current = predicate.getAsBoolean();
            if (current && !predicatePrevious) {
                toggleValue = !toggleValue;
            }
            predicatePrevious = current;
            return toggleValue;
        }
    }

    // utility class
    private Conditions() {}
}
