package com.team766.framework3;

import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase3;
import com.team766.framework3.FakeMechanism.FakeStatus;
import edu.wpi.first.wpilibj2.command.Command;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

public class ConditionsTest extends TestCase3 {
    private static class ValueProxy implements BooleanSupplier {
        boolean value = false;

        public boolean getAsBoolean() {
            return value;
        }
    }

    private static class ProxyRequest implements Request {
        boolean isDone = false;

        public boolean isDone() {
            return isDone;
        }
    }

    public record OtherStatus(int currentState) implements Status {}

    private static Command startContext(Consumer<Context> runnable) {
        var context = new ContextImpl(new FunctionalProcedure(Set.of(), runnable));
        context.initialize();
        return context;
    }

    private static boolean step(int numSteps, Command command) {
        for (int i = 0; i < 5; ++i) {
            command.execute();
        }
        return command.isFinished();
    }

    private static void finish(Command command) {
        while (!command.isFinished()) {
            command.execute();
            Thread.yield();
        }
    }

    @Test
    public void testWaitForValue() {
        AtomicReference<Optional<String>> container = new AtomicReference<>(Optional.empty());
        var c =
                startContext(
                        context -> {
                            assertEquals(
                                    "the value", Conditions.waitForValue(context, container::get));
                        });
        assertFalse(step(5, c));
        container.set(Optional.of("the value"));
        assertTrue(step(1, c));
    }

    @Test
    public void testWaitForValueOrTimeout() {
        AtomicReference<Optional<String>> container = new AtomicReference<>(Optional.empty());
        finish(
                startContext(
                        context -> {
                            assertEquals(
                                    Optional.empty(),
                                    Conditions.waitForValueOrTimeout(context, container::get, 0.1));
                        }));
        var c =
                startContext(
                        context -> {
                            assertEquals(
                                    Optional.of("the value"),
                                    Conditions.waitForValueOrTimeout(
                                            context, container::get, 1000.0));
                        });
        assertFalse(step(5, c));
        container.set(Optional.of("the value"));
        assertTrue(step(1, c));
    }

    @Test
    public void testCheckForStatus() {
        assertFalse(Conditions.checkForStatus(FakeStatus.class));
        StatusBus.publishStatus(new FakeStatus(0));
        assertTrue(Conditions.checkForStatus(FakeStatus.class));
        assertFalse(Conditions.checkForStatus(OtherStatus.class));
    }

    @Test
    public void testCheckForStatusWith() {
        assertFalse(Conditions.checkForStatusWith(FakeStatus.class, s -> s.currentState() == 1));
        StatusBus.publishStatus(new OtherStatus(1));
        assertFalse(Conditions.checkForStatusWith(FakeStatus.class, s -> s.currentState() == 1));
        StatusBus.publishStatus(new FakeStatus(0));
        assertFalse(Conditions.checkForStatusWith(FakeStatus.class, s -> s.currentState() == 1));
        StatusBus.publishStatus(new FakeStatus(1));
        assertTrue(Conditions.checkForStatusWith(FakeStatus.class, s -> s.currentState() == 1));
    }

    @Test
    public void testCheckForStatusEntryWith() {
        assertFalse(
                Conditions.checkForStatusEntryWith(
                        FakeStatus.class, s -> s.status().currentState() == 1));
        StatusBus.publishStatus(new OtherStatus(1));
        assertFalse(
                Conditions.checkForStatusEntryWith(
                        FakeStatus.class, s -> s.status().currentState() == 1));
        StatusBus.publishStatus(new FakeStatus(0));
        assertFalse(
                Conditions.checkForStatusEntryWith(
                        FakeStatus.class, s -> s.status().currentState() == 1));
        StatusBus.publishStatus(new FakeStatus(1));
        assertTrue(
                Conditions.checkForStatusEntryWith(
                        FakeStatus.class, s -> s.status().currentState() == 1));
    }

    @Test
    public void testWaitForStatus() {
        var c =
                startContext(
                        context -> {
                            assertEquals(
                                    new FakeStatus(42),
                                    Conditions.waitForStatus(context, FakeStatus.class));
                        });
        assertFalse(step(5, c));
        StatusBus.publishStatus(new OtherStatus(42));
        assertFalse(step(5, c));
        StatusBus.publishStatus(new FakeStatus(42));
        assertTrue(step(1, c));
    }

    @Test
    public void testWaitForStatusOrTimeout() {
        finish(
                startContext(
                        context -> {
                            assertEquals(
                                    Optional.empty(),
                                    Conditions.waitForStatusOrTimeout(
                                            context, FakeStatus.class, 0.1));
                        }));
        var c =
                startContext(
                        context -> {
                            assertEquals(
                                    Optional.of(new FakeStatus(42)),
                                    Conditions.waitForStatusOrTimeout(
                                            context, FakeStatus.class, 1000.0));
                        });
        assertFalse(step(5, c));
        StatusBus.publishStatus(new OtherStatus(42));
        assertFalse(step(5, c));
        StatusBus.publishStatus(new FakeStatus(42));
        assertTrue(step(1, c));
    }

    @Test
    public void testWaitForStatusWith() {
        var c =
                startContext(
                        context -> {
                            assertEquals(
                                    new FakeStatus(42),
                                    Conditions.waitForStatusWith(
                                            context,
                                            FakeStatus.class,
                                            s -> s.currentState() == 42));
                        });
        assertFalse(step(5, c));
        StatusBus.publishStatus(new OtherStatus(42));
        assertFalse(step(5, c));
        StatusBus.publishStatus(new FakeStatus(0));
        assertFalse(step(5, c));
        StatusBus.publishStatus(new FakeStatus(42));
        assertTrue(step(1, c));
    }

    @Test
    public void testWaitForStatusWithOrTimeout() {
        finish(
                startContext(
                        context -> {
                            assertEquals(
                                    Optional.empty(),
                                    Conditions.waitForStatusWithOrTimeout(
                                            context,
                                            FakeStatus.class,
                                            s -> s.currentState() == 42,
                                            0.1));
                        }));
        var c =
                startContext(
                        context -> {
                            assertEquals(
                                    Optional.of(new FakeStatus(42)),
                                    Conditions.waitForStatusWithOrTimeout(
                                            context,
                                            FakeStatus.class,
                                            s -> s.currentState() == 42,
                                            1000.0));
                        });
        assertFalse(step(5, c));
        StatusBus.publishStatus(new OtherStatus(42));
        assertFalse(step(5, c));
        StatusBus.publishStatus(new FakeStatus(0));
        assertFalse(step(5, c));
        StatusBus.publishStatus(new FakeStatus(42));
        assertTrue(step(1, c));
    }

    @Test
    public void testWaitForRequest() {
        var request = new ProxyRequest();
        var c =
                startContext(
                        context -> {
                            Conditions.waitForRequest(context, request);
                        });
        assertFalse(step(5, c));
        request.isDone = true;
        assertTrue(step(1, c));
    }

    @Test
    public void testWaitForRequestOrTimeout() {
        var request = new ProxyRequest();
        finish(
                startContext(
                        context -> {
                            assertFalse(Conditions.waitForRequestOrTimeout(context, request, 0.1));
                        }));
        var c =
                startContext(
                        context -> {
                            assertTrue(
                                    Conditions.waitForRequestOrTimeout(context, request, 1000.0));
                        });
        assertFalse(step(5, c));
        request.isDone = true;
        assertTrue(step(1, c));
    }

    @Test
    public void testToggle() {
        var v = new ValueProxy();

        var t = new Conditions.Toggle(v);

        assertFalse(t.getAsBoolean());
        assertFalse(t.getAsBoolean());

        v.value = true;

        assertTrue(t.getAsBoolean());
        assertTrue(t.getAsBoolean());

        v.value = false;

        assertTrue(t.getAsBoolean());
        assertTrue(t.getAsBoolean());

        v.value = true;

        assertFalse(t.getAsBoolean());
        assertFalse(t.getAsBoolean());

        v.value = false;

        assertFalse(t.getAsBoolean());
        assertFalse(t.getAsBoolean());
    }
}
