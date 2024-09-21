package com.team766.framework3;

import com.team766.logging.Category;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bus for broadcasting and querying {@link Status} of different parts of the robot (eg state of a Mechanism).
 * This is a Singleton, accessed via {@link #getInstance()}.  Producers can call {@link #publish} to publish
 * their latest {@link Status}.  Consumers can call {@link #getStatus(Class)} with the class Object
 * (eg, {@code MyStatus.class}) for the {@link Status} they are interested in querying, to get the latest
 * published {@link Status}.
 */
public class StatusBus implements LoggingBase {

    private static StatusBus s_instance = new StatusBus();
    private final Map<Class<? extends Status>, Status> statuses = new LinkedHashMap<>();

    /**
     * Get the Singleton instance of the {@link StatusBus}.
     */
    public static StatusBus getInstance() {
        return s_instance;
    }

    // TODO: would this be helpful?
    // private void clear() {
    //     statuses.clear();
    // }

    /**
     * Publish a new {@link Status} for the given specific class of {@link Status}.  Each producer will
     * create their own implementation of the {@link Status} interface to contain its state information.
     *
     * This method also logs the Status to diagnostic logs.
     */
    public void publish(Status status) {
        statuses.put(status.getClass(), status);
        // TODO: also publish to data logs
        log("StatusBus received Status (" + status.getClass().getName() + "): " + status);
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
    public <S extends Status> S getStatus(Class<S> statusClass) {
        return (S) statuses.get(statusClass);
    }

    @Override
    public Category getLoggerCategory() {
        return Category.FRAMEWORK;
    }
}
