package com.team766.framework3;

import com.team766.library.Holder;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Pre-canned Conditions used frqeuently in robot programming.
 */
public class Conditions {
    public static <T> T waitFor(Context context, Supplier<Optional<T>> supplier) {
        final Holder<T> result = new Holder<>();
        context.waitFor(
                () -> {
                    final var t = supplier.get();
                    t.ifPresent(result::set);
                    return t.isPresent();
                });
        return result.value;
    }

    public static <T> Optional<T> waitForValueOrTimeout(
            Context context, Supplier<Optional<T>> supplier, double timeoutSeconds) {
        final Holder<Optional<T>> result = new Holder<>(Optional.empty());
        context.waitForConditionOrTimeout(
                () -> {
                    result.value = supplier.get();
                    return result.value.isPresent();
                },
                timeoutSeconds);
        return result.value;
    }

    public static <S extends Status> boolean checkForStatus(Class<S> statusClass) {
        return StatusBus.getStatusEntry(statusClass).isPresent();
    }

    /**
     * Predicate that checks whether or not the latest {@link Status} passes the
     * check provided by {@code predicate}.
     */
    public static <S extends Status> boolean checkForStatusWith(
            Class<S> statusClass, Predicate<S> predicate) {
        return StatusBus.getStatusValue(statusClass, predicate::test).orElse(false);
    }

    public static <S extends Status> boolean checkForStatusEntryWith(
            Class<S> statusClass, Predicate<StatusBus.Entry<S>> predicate) {
        return StatusBus.getStatusEntry(statusClass).map(predicate::test).orElse(false);
    }

    public static <T extends Status> T waitForStatus(Context context, Class<T> statusClass) {
        return waitFor(context, () -> StatusBus.getStatus(statusClass));
    }

    public static <T extends Status> Optional<T> waitForStatusOrTimeout(
            Context context, Class<T> statusClass, double timeoutSeconds) {
        return waitForValueOrTimeout(
                context, () -> StatusBus.getStatus(statusClass), timeoutSeconds);
    }

    public static <T extends Status> T waitForStatus(
            Context context, Class<T> statusClass, Predicate<T> predicate) {
        return waitFor(context, () -> StatusBus.getStatus(statusClass).filter(predicate));
    }

    public static <T extends Status> Optional<T> waitForStatusOrTimeout(
            Context context, Class<T> statusClass, Predicate<T> predicate, double timeoutSeconds) {
        return waitForValueOrTimeout(
                context, () -> StatusBus.getStatus(statusClass).filter(predicate), timeoutSeconds);
    }

    public static void waitForRequest(Context context, Request request) {
        context.waitFor(request::isDone);
    }

    public static void waitForRequestOrTimeout(
            Context context, Request request, double timeoutSeconds) {
        context.waitForConditionOrTimeout(request::isDone, timeoutSeconds);
    }

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
