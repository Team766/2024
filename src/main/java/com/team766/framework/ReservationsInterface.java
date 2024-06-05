package com.team766.framework;

import com.team766.library.ReflectionUtils;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.function.Consumer;

public interface ReservationsInterface {
    public interface EntryPoint<R, P> {
        R makeImplementation(GenericRobotSystemProvider provider);

        void addReservations(R r, Consumer<Subsystem> add);

        void applyReservations(R r, P p, Set<Subsystem> s);
    }

    @SuppressWarnings("unchecked")
    static <R, P> EntryPoint<R, P> makeEntryPoint(Class<?> reservationsType) {
        try {
            var entryPointClass =
                    Class.forName(reservationsType.getCanonicalName() + ".EntryPoint");
            return (EntryPoint<R, P>) entryPointClass.getConstructor().newInstance();
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException
                | ClassNotFoundException ex) {
            throw ReflectionUtils.sneakyThrow(ex);
        }
    }
}
