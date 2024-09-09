package com.team766.framework3;

import java.util.Set;

public interface RunnableWithContext {
    void run(Context context);

    Set<Mechanism<?>> reservations();
}
