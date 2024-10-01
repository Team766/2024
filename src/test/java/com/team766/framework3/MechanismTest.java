package com.team766.framework3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase3;
import com.team766.framework3.FakeMechanism.FakeRequest;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class MechanismTest extends TestCase3 {
    /// Test sending requests to a Mechanism. Also test that checkContextReservation succeeds when
    /// called from a Procedure which reserves the Mechanism.
    @Test
    public void testRequests() {
        var mech = new FakeMechanism();

        var cmd =
                new ContextImpl(
                        new FunctionalProcedure(
                                Set.of(mech),
                                context -> {
                                    // Step 1
                                    context.yield();

                                    // Step 2
                                    mech.setRequest(new FakeRequest(0));
                                    context.yield();

                                    // Step 3
                                    context.yield();

                                    // Step 4
                                    mech.setRequest(new FakeRequest(1));
                                    context.yield();
                                }));
        cmd.schedule();

        // Step 0. The CommandScheduler runs Subsystems (Mechanisms) before Commands (Procedures),
        // but this test is written as if the Procedure steps first. Thus we add a "Step 0" here
        // but not in the Procedure to readjust the relationship between these sequences of events.
        step();

        // Step 1. Test running the Mechanism in its uninitialized state.
        step();
        assertEquals(mech.getInitialRequest(), mech.currentRequest);
        assertFalse(mech.wasRequestNew);

        // Step 2. The Mechanism receives the first request.
        step();
        assertEquals(new FakeRequest(0), mech.currentRequest);
        assertTrue(mech.wasRequestNew);

        // Step 3. The Mechanism continues with its first request.
        step();
        assertEquals(new FakeRequest(0), mech.currentRequest);
        assertFalse(mech.wasRequestNew);

        // Step 4. The Mechanism receives the second request.
        step();
        assertEquals(new FakeRequest(1), mech.currentRequest);
        assertTrue(mech.wasRequestNew);

        // Poke the Procedure to ensure it has finished.
        step();
        assertTrue(cmd.isFinished());
    }

    /// Test that checkContextReservation throws an exception when called from a Procedure which has
    /// not reserved the Mechanism.
    @Test
    public void testFailedCheckContextReservationInProcedure() {
        class DummyMechanism extends Mechanism<FakeRequest> {
            @Override
            protected FakeRequest getInitialRequest() {
                return new FakeRequest(-1);
            }

            @Override
            protected void run(FakeRequest request, boolean isRequestNew) {}
        }
        var mech = new DummyMechanism();

        var thrownException = new AtomicReference<String>(null);
        var cmd =
                new ContextImpl(
                        new FunctionalProcedure(
                                Set.of(),
                                context -> {
                                    try {
                                        mech.setRequest(new FakeRequest(0));
                                    } catch (Throwable ex) {
                                        thrownException.set(ex.getMessage());
                                    }
                                }));
        cmd.schedule();
        step();
        assertThat(thrownException.get())
                .matches("DummyMechanism tried to be used without reserving it");

        var cmd2 = new ContextImpl(new FakeProcedure(1, Set.of(mech)));
        cmd2.schedule();
        cmd.schedule();
        step();
        assertThat(thrownException.get())
                .matches("DummyMechanism tried to be used without reserving it");
    }

    /// Test that checkContextReservation succeeds when called from within the Mechanism's own run()
    /// method.
    @Test
    public void testCheckContextReservationInRun() {
        var thrownException = new AtomicReference<Throwable>();
        @SuppressWarnings("unused")
        var mech =
                new FakeMechanism() {
                    @Override
                    protected void run(FakeRequest request, boolean isRequestNew) {
                        try {
                            checkContextReservation();
                        } catch (Throwable ex) {
                            thrownException.set(ex);
                        }
                    }
                };
        step();
        assertNull(thrownException.get());
    }

    /// Test that the Initial request runs after Mechanism creation.
    @Test
    public void testInitialRequest() {
        var mech = new FakeMechanism();
        step();
        assertEquals(new FakeRequest(-1), mech.currentRequest);
        assertTrue(mech.wasRequestNew);
        // Subsequent steps should continue to pass the Initial request to run(),
        // but it should not be indicated as a new request.
        mech.currentRequest = null;
        step();
        assertEquals(new FakeRequest(-1), mech.currentRequest);
        assertFalse(mech.wasRequestNew);
    }

    /// Test that the Idle request runs if no other Command reserves this Mechanism.
    @Test
    public void testIdleRequest() {
        var mech =
                new FakeMechanism() {
                    @Override
                    protected FakeRequest getIdleRequest() {
                        return new FakeRequest(0);
                    }
                };

        // The first step should run the Initial request.
        step();
        assertEquals(new FakeRequest(-1), mech.currentRequest);
        assertTrue(mech.wasRequestNew);
        // On subsequent steps, the Idle request should take over (as long as a Command hasn't
        // reserved this Mechanism).
        step();
        assertEquals(new FakeRequest(0), mech.currentRequest);
        assertTrue(mech.wasRequestNew);
        // Subsequent steps should continue to pass the Idle request to run(),
        // but it should not be indicated as a new request.
        mech.currentRequest = null;
        step();
        assertEquals(new FakeRequest(0), mech.currentRequest);
        assertFalse(mech.wasRequestNew);

        // When a Command is scheduled which reserves this Procedure, it should preempt
        // the Idle request.
        new FunctionalProcedure(
                        Set.of(mech),
                        context -> {
                            mech.setRequest(new FakeRequest(1));
                            context.waitFor(() -> false);
                        })
                .createCommandToRunProcedure()
                .schedule();
        step();
        step(); // NOTE: Second step() is needed because Scheduler runs Procedures after Subsystems
        assertEquals(new FakeRequest(1), mech.currentRequest);
        assertTrue(mech.wasRequestNew);
        // Subsequent steps should allow the scheduled Command to continue. It should not be
        // interrupted by the Idle request, even if the scheduled Command does not set a
        // new request.
        mech.currentRequest = null;
        step();
        assertEquals(new FakeRequest(1), mech.currentRequest);
        assertFalse(mech.wasRequestNew);
    }
}
