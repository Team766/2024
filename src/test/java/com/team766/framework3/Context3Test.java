package com.team766.framework3;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase3;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class Context3Test extends TestCase3 {
    /// Test basic operation of a procedure running in a Context.
    @Test
    public void testBasic() {
        var mech1 = new FakeMechanism1();
        var mech2 = new FakeMechanism2();

        var proc = new FakeProcedure(2, Set.of(mech1, mech2));
        var context = new ContextImpl(proc);

        assertEquals(Set.of(mech1, mech2), context.getRequirements());

        // Schedule the Context for execution by the scheduler.
        context.schedule();
        assertEquals(0, proc.age());
        assertFalse(proc.isEnded());
        assertFalse(context.isDone());

        // First step. The procedure does one step of work and then yields.
        step();
        assertEquals(1, proc.age());
        assertFalse(proc.isEnded());
        assertFalse(context.isDone());

        // Second step. The procedure does another step of work and then yields.
        step();
        assertEquals(2, proc.age());
        assertFalse(proc.isEnded());
        assertFalse(context.isDone());

        // Third step. The procedure finishes successfully and the Context ends.
        step();
        assertEquals(2, proc.age());
        assertTrue(proc.isEnded());
        assertTrue(context.isDone());
    }

    /// Test early termination of a procedure running in a Context.
    @Test
    public void testCancel() {
        var proc = new FakeProcedure(100, Set.of());
        var context = new ContextImpl(proc);
        context.schedule();

        // Do one normal step to allow the procedure to start running.
        step();
        assertEquals(1, proc.age());
        assertFalse(proc.isEnded());
        assertFalse(context.isDone());

        // After calling cancel(), the procedure should not run again, but should clean up.
        context.cancel();
        assertTrue(proc.isEnded());
        assertTrue(context.isDone());
        step();
        assertEquals(1, proc.age());
        assertTrue(proc.isEnded());
        assertTrue(context.isDone());
    }

    /// Regression test: calling cancel() on a Context before it is allowed to
    /// run the first time should not crash the program.
    @Test
    public void testCancelOnFirstTick() {
        var proc = new FakeProcedure(100, Set.of());
        var context = new ContextImpl(proc);

        context.schedule();
        assertFalse(proc.isEnded());
        assertFalse(context.isDone());

        // When calling cancel() before the procedure has started running, the procedure should
        // not run but the Context should end.
        context.cancel();
        assertTrue(context.isDone());
        step();
        assertEquals(0, proc.age());
        assertFalse(proc.isEnded()); /* False because proc never started running, so it didn't
                                        enter the try-finally in FakeProcedure. */
        assertTrue(context.isDone());
    }

    /// Test Context.runSync
    @Test
    public void testRunSync() {
        var mech1 = new FakeMechanism1();
        var mech2 = new FakeMechanism2();
        var proc1 = new FakeProcedure(1, Set.of(mech1));
        var proc2 = new FakeProcedure(1, Set.of(mech1, mech2));

        var proc =
                new FunctionalProcedure(
                        Set.of(mech1, mech2),
                        context -> {
                            context.runSync(proc1);
                            context.runSync(proc2);
                        });
        var context = new ContextImpl(proc);

        // Schedule the Context for execution by the scheduler.
        context.schedule();
        assertEquals(0, proc1.age());
        assertEquals(0, proc2.age());
        assertFalse(proc1.isEnded());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // First step. proc1 does one step of work and then yields.
        step();
        assertEquals(1, proc1.age());
        assertEquals(0, proc2.age());
        assertFalse(proc1.isEnded());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // Second step. proc1 finishes and proc2 does one step of work and then yields.
        step();
        assertEquals(1, proc1.age());
        assertEquals(1, proc2.age());
        assertTrue(proc1.isEnded());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // Third step. proc2 finishes and the Context ends.
        step();
        assertEquals(1, proc1.age());
        assertEquals(1, proc2.age());
        assertTrue(proc1.isEnded());
        assertTrue(proc2.isEnded());
        assertTrue(context.isDone());
    }

    /// Test Context.runParallel
    @Test
    public void testRunParallel() {
        var mech1 = new FakeMechanism1();
        var mech2 = new FakeMechanism2();
        var proc1 = new FakeProcedure(1, Set.of(mech1));
        var proc2 = new FakeProcedure(2, Set.of(mech2));

        var proc =
                new FunctionalProcedure(
                        Set.of(mech1, mech2),
                        context -> {
                            context.runParallel(proc1, proc2);
                        });
        var context = new ContextImpl(proc);

        // Schedule the Context for execution by the scheduler.
        context.schedule();
        assertEquals(0, proc1.age());
        assertEquals(0, proc2.age());
        assertFalse(proc1.isEnded());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // First step. proc1 and proc2 each do one step of work and then yield.
        step();
        assertEquals(1, proc1.age());
        assertEquals(1, proc2.age());
        assertFalse(proc1.isEnded());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // Second step. proc1 finishes; proc2 does one step of work and then yields.
        step();
        assertEquals(1, proc1.age());
        assertEquals(2, proc2.age());
        assertTrue(proc1.isEnded());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // Third step. proc2 finishes and the Context ends.
        step();
        assertEquals(1, proc1.age());
        assertEquals(2, proc2.age());
        assertTrue(proc1.isEnded());
        assertTrue(proc2.isEnded());
        assertTrue(context.isDone());
    }

    /// Test Context.runParallelRace
    @Test
    public void testRunParallelRace() {
        var mech1 = new FakeMechanism1();
        var mech2 = new FakeMechanism2();
        var proc1 = new FakeProcedure(1, Set.of(mech1));
        var proc2 = new FakeProcedure(3, Set.of(mech2));

        var proc =
                new FunctionalProcedure(
                        Set.of(mech1, mech2),
                        context -> {
                            context.runParallelRace(proc1, proc2);
                        });
        var context = new ContextImpl(proc);

        // Schedule the Context for execution by the scheduler.
        context.schedule();
        assertEquals(0, proc1.age());
        assertEquals(0, proc2.age());
        assertFalse(proc1.isEnded());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // First step. proc1 and proc2 each do one step of work and then yield.
        step();
        assertEquals(1, proc1.age());
        assertEquals(1, proc2.age());
        assertFalse(proc1.isEnded());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // Second step. proc1 finishes; proc2 is canceled; the Context ends.
        step();
        assertEquals(1, proc1.age());
        assertEquals(2, proc2.age());
        assertTrue(proc1.isEnded());
        assertTrue(proc2.isEnded());
        assertTrue(context.isDone());
    }

    /// Test calling Context.run* with a Procedure that tries to reserve a Mechanism which
    /// is not reserved by the parent Procedure. Should raise an exception.
    @ParameterizedTest
    @MethodSource("paramsRunWithMissingReservation")
    public void testRunWithMissingReservation(BiConsumer<Context, Procedure> runMethod) {
        var mech = new FakeMechanism();
        var proc1 = new FakeProcedure(1, Set.of(mech));

        AtomicReference<String> thrownException = new AtomicReference<>(null);
        var proc =
                new FunctionalProcedure(
                        Set.of(),
                        context -> {
                            try {
                                runMethod.accept(context, proc1);
                            } catch (IllegalArgumentException ex) {
                                thrownException.set(ex.getMessage());
                            }
                        });
        var context = new ContextImpl(proc);

        context.schedule();
        step();

        assertThat(thrownException.get())
                .matches(
                        ".*Context3Test\\$\\$Lambda.* tried to run .*FakeProcedure.* but is missing the reservation on FakeMechanism");
    }

    static Stream<BiConsumer<Context, Procedure>> paramsRunWithMissingReservation() {
        return Stream.of(Context::runSync, Context::runParallel, Context::runParallelRace);
    }

    /// Test calling Context.run* with Procedures that try to reserve the same Mechanism.
    /// Should raise an exception.
    @ParameterizedTest
    @MethodSource("paramsRunWithConflictingReservation")
    public void testRunWithConflictingReservation(BiConsumer<Context, Procedure[]> runMethod) {
        var mech = new FakeMechanism();
        var proc1 = new FakeProcedure(1, Set.of(mech));
        var proc2 = new FakeProcedure(1, Set.of(mech));

        AtomicReference<String> thrownException = new AtomicReference<>(null);
        var proc =
                new FunctionalProcedure(
                        Set.of(mech),
                        context -> {
                            try {
                                runMethod.accept(context, new Procedure[] {proc1, proc2});
                            } catch (IllegalArgumentException ex) {
                                thrownException.set(ex.getMessage());
                            }
                        });
        var context = new ContextImpl(proc);

        context.schedule();
        step();

        assertThat(thrownException.get())
                .matches(
                        "Multiple commands in a parallel composition cannot require the same subsystems");
    }

    static Stream<BiConsumer<Context, Procedure[]>> paramsRunWithConflictingReservation() {
        return Stream.of(Context::runParallel, Context::runParallelRace);
    }

    /// Test Context.startAsync
    @Test
    public void testStartAsync() {
        var mech = new FakeMechanism1();
        var proc2 = new FakeProcedure(1, Set.of(mech));

        var proc1age = new AtomicInteger(0);
        var proc1 =
                new FunctionalProcedure(
                        Set.of(),
                        context -> {
                            var lc = context.startAsync(proc2);
                            while (!lc.isDone()) {
                                proc1age.incrementAndGet();
                                context.yield();
                            }
                        });
        var context = new ContextImpl(proc1);

        // Schedule the Context for execution by the scheduler.
        context.schedule();
        assertEquals(0, proc1age.get());
        assertEquals(0, proc2.age());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // First step. proc1 launches proc2; proc1age incremented.
        step();
        assertEquals(1, proc1age.get());
        assertEquals(0, proc2.age());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // Second step. proc1 and proc2 each do one step of work and then yield.
        step();
        assertEquals(2, proc1age.get());
        assertEquals(1, proc2.age());
        assertFalse(proc2.isEnded());
        assertFalse(context.isDone());

        // Third step. proc1 does one step of work; proc2 finishes and the Context ends.
        step();
        assertEquals(3, proc1age.get());
        assertEquals(1, proc2.age());
        assertTrue(proc2.isEnded());
        assertFalse(context.isDone());

        // Fourth step. proc1 finishes and the Context ends.
        step();
        assertEquals(3, proc1age.get());
        assertEquals(1, proc2.age());
        assertTrue(proc2.isEnded());
        assertTrue(context.isDone());
    }

    /// Test calling Context.startAsync with a Procedure that tries to reserve a Mechanism which
    /// is also reserved by the parent Procedure. Should raise an exception.
    @Test
    public void testStartAsyncWithConflictingReservation() {
        var mech = new FakeMechanism();
        var proc2 = new FakeProcedure(1, Set.of(mech));

        AtomicReference<String> thrownException = new AtomicReference<>(null);
        var proc1 =
                new FunctionalProcedure(
                        Set.of(mech),
                        context -> {
                            try {
                                context.startAsync(proc2);
                            } catch (IllegalArgumentException ex) {
                                thrownException.set(ex.getMessage());
                            }
                        });
        var context = new ContextImpl(proc1);

        context.schedule();
        step();

        assertThat(thrownException.get())
                .matches(
                        ".*Context3Test\\$\\$Lambda.* tried to launch .*FakeProcedure.* asynchronously, but both have a reservation on FakeMechanism");
    }
}
