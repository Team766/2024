package com.team766.framework;

import com.team766.logging.Category;
import com.team766.logging.LoggerExceptionUtils;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public abstract class LightsBase implements LoggingBase {
    private final Map<Class<?>, ContextImpl> prevScheduledCommands = new HashMap<>();
    private final Set<Class<?>> scheduledCommands = new HashSet<>();

    protected abstract void dispatch(Statuses statuses);

    @Override
    public Category getLoggerCategory() {
        return Category.OPERATOR_INTERFACE;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    protected void runAnimation(Consumer<Context> animation) {
        var handle = animation.getClass();
        if (!handle.isAnonymousClass()) {
            throw new IllegalArgumentException("Argument to runAnimation should be a lambda");
        }
        if (scheduledCommands.contains(handle)) {
            throw new IllegalStateException(
                    "A single animation lambda was used more than once. This is not supported.");
        }
        ContextImpl command = prevScheduledCommands.get(handle);
        if (command == null || command.isFinished()) {
            command = new ContextImpl(new ProcedureInterface() {
                @Override
                public void execute(Context context) {
                    animation.accept(context);
                }

                @Override
                public Set<Subsystem> getReservations() {
                    return Set.of();
                }
            });
            command.initialize();
        }
        command.execute();
        prevScheduledCommands.put(handle, command);
        scheduledCommands.add(handle);
    }

    public final void run() {
        try {
            scheduledCommands.clear();

            dispatch(Statuses.getInstance());

            prevScheduledCommands.entrySet().removeIf(entry -> {
                if (scheduledCommands.contains(entry.getKey())) {
                    return false;
                }
                entry.getValue().end(true);
                return true;
            });
        } catch (Exception ex) {
            LoggerExceptionUtils.logException(ex);
        }
    }
}
