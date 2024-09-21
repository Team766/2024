package com.team766.framework3;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase3;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class AdvancedUtilsTest extends TestCase3 {
    @Test
    public void testStartAsync() {
        var mech = new FakeMechanism1();
        var proc2 = new FakeProcedure(1, Set.of(mech));

        var proc1age = new AtomicInteger(0);
        var proc1 =
                new FunctionalProcedure(
                        Set.of(),
                        context -> {
                            var lc = AdvancedUtils.startAsync(context, proc2);
                            while (!lc.isFinished()) {
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
        assertFalse(context.isFinished());

        // First step. proc1 launches proc2; proc1age incremented.
        step();
        assertEquals(1, proc1age.get());
        assertEquals(0, proc2.age());
        assertFalse(proc2.isEnded());
        assertFalse(context.isFinished());

        // Second step. proc1 and proc2 each do one step of work and then yield.
        step();
        assertEquals(2, proc1age.get());
        assertEquals(1, proc2.age());
        assertFalse(proc2.isEnded());
        assertFalse(context.isFinished());

        // Third step. proc1 does one step of work; proc2 finishes and the Context ends.
        step();
        assertEquals(3, proc1age.get());
        assertEquals(1, proc2.age());
        assertTrue(proc2.isEnded());
        assertFalse(context.isFinished());

        // Fourth step. proc1 finishes and the Context ends.
        step();
        assertEquals(3, proc1age.get());
        assertEquals(1, proc2.age());
        assertTrue(proc2.isEnded());
        assertTrue(context.isFinished());
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
                                AdvancedUtils.startAsync(context, proc2);
                            } catch (IllegalArgumentException ex) {
                                thrownException.set(ex.getMessage());
                            }
                        });
        var context = new ContextImpl(proc1);

        context.schedule();
        step();

        assertThat(thrownException.get())
                .matches(
                        ".*AdvancedUtilsTest\\$\\$Lambda.* tried to launch .*FakeProcedure.* asynchronously, but both have a reservation on FakeMechanism");
    }
}
