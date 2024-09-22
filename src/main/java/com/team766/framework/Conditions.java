package com.team766.framework;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Pre-canned Conditions used frqeuently in robot programming.
 */
public class Conditions {
    /**
     * Suspend the Procedure until the given Supplier returns a non-empty Optional. Then, unwrap
     * the T value from the Optional and return it.
     */
    public static <T> T waitForValue(Context context, Supplier<Optional<T>> supplier) {
        final AtomicReference<T> result = new AtomicReference<>();
        context.waitFor(() -> {
            final var t = supplier.get();
            t.ifPresent(result::set);
            return t.isPresent();
        });
        return result.get();
    }

    /**
     * Suspend the Procedure until the given Supplier returns a non-empty Optional, or we've waited
     * for at least {@code timeoutSeconds}. Returns the last value returned by the Supplier.
     */
    public static <T> Optional<T> waitForValueOrTimeout(
            Context context, Supplier<Optional<T>> supplier, double timeoutSeconds) {
        final AtomicReference<Optional<T>> result = new AtomicReference<>(Optional.empty());
        context.waitForConditionOrTimeout(
                () -> {
                    result.set(supplier.get());
                    return result.get().isPresent();
                },
                timeoutSeconds);
        return result.get();
    }

    /**
     * Predicate that checks whether or not a {@link Status} with the given class has been published
     */
    public static <S extends Status> boolean checkForStatus(Class<S> statusClass) {
        return StatusBus.getStatusEntry(statusClass).isPresent();
    }

    /**
     * Predicate that checks whether or not the latest {@link Status} with the given class passes
     * the check provided by {@code predicate}.
     */
    public static <S extends Status> boolean checkForStatusWith(
            Class<S> statusClass, Predicate<S> predicate) {
        return StatusBus.getStatusValue(statusClass, predicate::test).orElse(false);
    }

    /**
     * Predicate that checks whether or not the latest {@link Status} with the given class passes
     * the check provided by {@code predicate}, including additional metadata about how the Status
     * was published.
     */
    public static <S extends Status> boolean checkForStatusEntryWith(
            Class<S> statusClass, Predicate<StatusBus.Entry<S>> predicate) {
        return StatusBus.getStatusEntry(statusClass).map(predicate::test).orElse(false);
    }

    /**
     * Suspend the Procedure until a {@link Status} with the given class has been published, then
     * return that Status.
     */
    public static <T extends Status> T waitForStatus(Context context, Class<T> statusClass) {
        return waitForValue(context, () -> StatusBus.getStatus(statusClass));
    }

    /**
     * Suspend the Procedure until a {@link Status} with the given class has been published, or
     * we've waited for at least {@code timeoutSeconds}. Returns an Optional containing the Status
     * if one was published, or return an empty Optional if the timeout was reached.
     */
    public static <T extends Status> Optional<T> waitForStatusOrTimeout(
            Context context, Class<T> statusClass, double timeoutSeconds) {
        return waitForValueOrTimeout(
                context, () -> StatusBus.getStatus(statusClass), timeoutSeconds);
    }

    /**
     * Suspend the Procedure until a {@link Status} with the given class has been published that
     * passes the check provided by {@code predicate}, then return that Status.
     */
    public static <T extends Status> T waitForStatusWith(
            Context context, Class<T> statusClass, Predicate<T> predicate) {
        return waitForValue(context, () -> StatusBus.getStatus(statusClass).filter(predicate));
    }

    /**
     * Suspend the Procedure until a {@link Status} with the given class has been published that
     * passes the check provided by {@code predicate}, or we've waited for at least
     * {@code timeoutSeconds}. Returns an Optional containing the Status if one was published, or
     * return an empty Optional if the timeout was reached.
     */
    public static <T extends Status> Optional<T> waitForStatusWithOrTimeout(
            Context context, Class<T> statusClass, Predicate<T> predicate, double timeoutSeconds) {
        return waitForValueOrTimeout(
                context, () -> StatusBus.getStatus(statusClass).filter(predicate), timeoutSeconds);
    }

    /**
     * Suspend the Procedure until {@link Request#isDone} returns true.
     */
    public static void waitForRequest(Context context, Request request) {
        context.waitFor(request::isDone);
    }

    /**
     * Suspend the Procedure until {@link Request#isDone} returns true, or we've waited for at least
     * {@code timeoutSeconds}. Returns true if the Request is done; false otherwise.
     */
    public static boolean waitForRequestOrTimeout(
            Context context, Request request, double timeoutSeconds) {
        return context.waitForConditionOrTimeout(request::isDone, timeoutSeconds);
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
