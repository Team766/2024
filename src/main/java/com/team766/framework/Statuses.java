package com.team766.framework;

import com.team766.hal.RobotProvider;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class Statuses implements Iterable<Statuses.Entry<?>> {
    public interface StatusSource {
        boolean isStatusActive();
    }

    public static class Entry<T extends Record> {
        public final T status;
        public final double timestamp;
        public final WeakReference<StatusSource> source;

        public Entry(T status, StatusSource source) {
            this.status = status;
            this.timestamp = RobotProvider.instance.getClock().getTime();
            this.source = new WeakReference<Statuses.StatusSource>(source);
        }

        public double age() {
            return RobotProvider.instance.getClock().getTime() - timestamp;
        }

        public boolean isFresh() {
            var src = source.get();
            return src != null && src.isStatusActive();
        }

        public boolean isFreshOrAgeLessThan(double maxAge) {
            return isFresh() || age() < maxAge;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Entry<?> other) {
                return status.equals(other.status) && source == other.source;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(status, source);
        }

        @Override
        public String toString() {
            return status + " @ " + timestamp;
        }
    }

    private static final Statuses instance = new Statuses();

    /* package */ static Statuses getInstance() {
        return instance;
    }

    public static <StatusRecord extends Record> Optional<StatusRecord> getStatus(
            Class<StatusRecord> c) {
        return Statuses.getInstance().get(c).map(s -> s.status);
    }

    public static <StatusRecord extends Record> Optional<Entry<StatusRecord>> getStatusEntry(
            Class<StatusRecord> c) {
        return Statuses.getInstance().get(c);
    }

    private final LinkedList<Entry<?>> data = new LinkedList<>();

    private Entry<?> add(Entry<?> entry) {
        final var previous = getFirst(entry::equals);
        if (previous.isPresent()) {
            return previous.get();
        }
        remove(entry.status.getClass());
        data.addFirst(entry);
        return entry;
    }

    public Entry<?> add(Record status, StatusSource source) {
        return add(new Entry<>(status, source));
    }

    public void clear() {
        data.clear();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public Iterator<Entry<?>> iterator() {
        return data.iterator();
    }

    public Optional<Entry<?>> getFirst() {
        return isEmpty() ? Optional.empty() : Optional.of(data.getFirst());
    }

    @SuppressWarnings("unchecked")
    public final <T extends Record> Optional<Entry<T>> get(Class<T> statusClass) {
        return data.stream()
                .filter(s -> statusClass.isInstance(s.status))
                .map(s -> (Entry<T>) s)
                .findFirst();
    }

    public final Optional<Entry<?>> getFirst(Predicate<Entry<?>> predicate) {
        return data.stream().filter(predicate).findFirst();
    }

    @SafeVarargs
    public final Optional<Entry<?>> getFirst(Class<? extends Record>... statusClasses) {
        return data.stream()
                .filter(
                        s -> {
                            for (var statusClass : statusClasses) {
                                if (statusClass.isInstance(s.status)) {
                                    return true;
                                }
                            }
                            return false;
                        })
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    public final <T extends Record> Optional<Entry<T>> get(
            Class<T> statusClass, Predicate<Entry<T>> predicate) {
        return data.stream()
                .filter(s -> statusClass.isInstance(s.status))
                .map(s -> (Entry<T>) s)
                .filter(predicate)
                .findFirst();
    }

    public final boolean has(Class<? extends Record> statusClass) {
        return get(statusClass).isPresent();
    }

    public final boolean has(Predicate<Entry<?>> predicate) {
        return getFirst(predicate).isPresent();
    }

    public final <T extends Record> boolean has(
            Class<T> statusClass, Predicate<Entry<T>> predicate) {
        return get(statusClass, predicate).isPresent();
    }

    public void remove(Entry<?> entry) {
        data.remove(entry);
    }

    public void remove(Class<? extends Record> statusClass) {
        data.removeIf(s -> statusClass.isInstance(s.status));
    }
}
