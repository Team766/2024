package com.team766.framework3;

import java.util.Collections;
import java.util.Set;

@FunctionalInterface
public interface RunnableWithContext {
    void run(Context context);

    default <M extends Mechanism<?>> M reserve(M mechanism) {
        throw new UnsupportedOperationException(
                "Default implementation does not support reserving mechanisms!");
    }

    default void release(Mechanism<?> mechanism) {
        throw new UnsupportedOperationException(
                "Default implementation does not support reserving mechanisms!");
    }

    default Set<Mechanism<?>> reservations() {
        return Collections.emptySet();
    }
}
