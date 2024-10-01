package com.team766.framework;

import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Bus for broadcasting and querying {@link Status} of different parts of the robot (eg state of a Mechanism).
 * This is a Singleton.  Producers can call {@link #publish} to publish their latest {@link Status}.
 * Consumers can call {@link #getStatus(Class)} with the class Object (eg, {@code MyStatus.class})
 * for the {@link Status} they are interested in querying, to get the latest published {@link Status}.
 */
public class StatusBus {

    public record Entry<T extends Status>(T status, double timestamp) {
        public double age() {
            return RobotProvider.instance.getClock().getTime() - timestamp;
        }
    }

    private static final Map<Class<?>, Entry<?>> statuses = new LinkedHashMap<>();

    // TODO(MF3): would this be helpful?
    // private static void clear() {
    //     statuses.clear();
    // }

    /**
     * Publish a new {@link Status} for the given specific class of {@link Status}.  Each producer will
     * create their own implementation of the {@link Status} interface to contain its state information.
     *
     * This method also logs the Status to diagnostic logs.
     */
    public static <S extends Record & Status> void publishStatus(S status) {
        statuses.put(
                status.getClass(),
                new Entry<>(status, RobotProvider.instance.getClock().getTime()));
        // TODO(MF3): also publish to data logs
        Logger.get(Category.FRAMEWORK)
                .logRaw(
                        Severity.INFO,
                        "StatusBus received Status ("
                                + status.getClass().getName()
                                + "): "
                                + status);
    }

    /**
     * Gets the latest published {@link Status} for the given class of {@link Status}.  Each producer will
     * create their own implementation of the {@link Status} interface to contain its state information.  Each
     * consumer will need to know the {@link Status} class a priori, in order to query it.
     *
     * @param <S> The specific {@link Status} class of interest.
     * @param statusClass The Class object for the Status, eg {@code MyStatus.class}.
     * @return The latest published {@link Status} or null if the {@link Status} hasn't been published.
     */
    @SuppressWarnings("unchecked")
    public static <S extends Status> Optional<Entry<S>> getStatusEntry(Class<S> statusClass) {
        return Optional.ofNullable((Entry<S>) statuses.get(statusClass));
    }

    public static <S extends Status> Optional<S> getStatus(Class<S> statusClass) {
        return getStatusEntry(statusClass).map(Entry<S>::status);
    }

    public static <S extends Status> S getStatusOrThrow(Class<S> statusClass) {
        return getStatus(statusClass).orElseThrow();
    }

    public static <S extends Status, V> Optional<V> getStatusValue(
            Class<S> statusClass, Function<S, V> getter) {
        return getStatusEntry(statusClass).map(s -> getter.apply(s.status()));
    }
}
