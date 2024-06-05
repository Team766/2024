package com.team766.framework.resources;

import com.github.meanbeanlib.mirror.Executables;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.library.function.Functions.Function2;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.HashMap;
import java.util.Map;

class Shooter implements Subsystem {}

class Intake implements Subsystem {}

class DriverShootVelocityAndIntake extends Procedure {
    public DriverShootVelocityAndIntake(Shooter shooter, Intake intake) {
        super(reservations(shooter, intake));
        System.out.println(shooter);
        System.out.println(intake);
    }

    public void run(Context context) {}
}

public class Test {

    public static class ResourceUnavailableException extends Exception {}

    private <T extends Subsystem> T reserveSubsystem(T t) throws ResourceUnavailableException {
        return t;
    }

    private Map<Class<? extends Subsystem>, Subsystem> subsystemRegistry = new HashMap<>();

    @SuppressWarnings("unchecked")
    private <T extends Subsystem> T lookupSubsystemInRegistry(Class<T> claz) {
        return (T) subsystemRegistry.computeIfAbsent(claz, clazz -> {
            if (Shooter.class.equals(clazz)) {
                return new Shooter();
            }
            if (Intake.class.equals(clazz)) {
                return new Intake();
            }
            throw new IllegalArgumentException(clazz.toString());
        });
    }

    private final <SubsystemT extends Subsystem> SubsystemT reserve(Class<SubsystemT> c)
            throws ResourceUnavailableException {
        return reserveSubsystem(lookupSubsystemInRegistry(c));
    }

    public interface CommandSupplier {
        Command get() throws ResourceUnavailableException;
    }

    protected final boolean tryRunning(CommandSupplier callback) {
        try {
            System.out.println(callback.get());
            return true;
        } catch (ResourceUnavailableException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    protected final <SubsystemA extends Subsystem, SubsystemB extends Subsystem>
            boolean ifAvailable(Function2<SubsystemA, SubsystemB, Command> callback) {
        var params = Executables.findExecutable(callback).getParameterTypes();
        Class<SubsystemA> a = (Class<SubsystemA>) params[0];
        Class<SubsystemB> b = (Class<SubsystemB>) params[1];
        return ifAvailable(a, b, callback);
    }

    protected final <SubsystemA extends Subsystem, SubsystemB extends Subsystem>
            boolean ifAvailable(
                    Class<SubsystemA> a,
                    Class<SubsystemB> b,
                    Function2<SubsystemA, SubsystemB, Command> callback) {
        SubsystemA aS;
        SubsystemB bS;
        try {
            aS = reserve(a);
            bS = reserve(b);
        } catch (ResourceUnavailableException e) {
            return false;
        }
        System.out.println(callback.apply(aS, bS));
        return true;
    }

    public void test() {
        System.out.println("\nProgressive:");
        tryRunning(() ->
                new DriverShootVelocityAndIntake(reserve(Shooter.class), reserve(Intake.class)));

        System.out.println("\nMagic:");
        ifAvailable((Shooter shooter, Intake intake) ->
                new DriverShootVelocityAndIntake(shooter, intake));

        ifAvailable(DriverShootVelocityAndIntake::new);

        System.out.println("\nExplicit:");
        ifAvailable(
                Shooter.class,
                Intake.class,
                (shooter, intake) -> new DriverShootVelocityAndIntake(shooter, intake));

        ifAvailable(Shooter.class, Intake.class, DriverShootVelocityAndIntake::new);
    }

    public static void main(String[] args) {
        new Test().test();
    }
}
