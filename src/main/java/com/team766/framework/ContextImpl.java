package com.team766.framework;

import com.team766.hal.Clock;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.LoggerExceptionUtils;
import com.team766.logging.Severity;
import edu.wpi.first.wpilibj2.command.Command;
import java.lang.StackWalker.StackFrame;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * See {@link Context} for a general description of the Context concept.
 *
 * Currently, threads of execution are implemented using OS threads, but this
 * should be considered an implementation detail and may change in the future.
 * Even though the framework creates multiple OS threads, it uses Java's
 * monitors to implement a "baton passing" pattern in order to ensure that only
 * one of threads is actually running at once (the others will be sleeping,
 * waiting for the baton to be passed to them).
 */
class ContextImpl<T> extends Command implements ContextWithValue<T>, LaunchedContextWithValue<T> {
    private Optional<T> m_lastYieldedValue = Optional.empty();

    /**
     * Represents the baton-passing state (see class comments). Instead of
     * passing a baton directly from one Context's thread to the next, each
     * Context has its own baton that gets passed from the program's main thread
     * to the Context's thread and back. While this is less efficient (double
     * the number of OS context switches required), it makes the code simpler
     * and more modular.
     */
    private enum ControlOwner {
        MAIN_THREAD,
        SUBROUTINE,
    }

    /**
     * Indicates the lifetime state of this Context.
     */
    private enum State {
        /**
         * The Context has not yet been started.
         */
        NEW,
        /**
         * The Context has been started.
         */
        RUNNING,
        /**
         * cancel() has been called on this Context (but it has not been allowed
         * to respond to the stop request yet).
         */
        CANCELED,
        /**
         * The Context's execution has come to an end.
         */
        DONE,
    }

    // package visible for testing
    /* package */ static class TimedPredicate implements BooleanSupplier {
        private final Clock clock;
        private final BooleanSupplier predicate;
        private final double deadlineSeconds;
        private boolean succeeded = false;

        // package visible for testing
        /* package */ TimedPredicate(
                Clock clock, BooleanSupplier predicate, double timeoutSeconds) {
            this.clock = clock;
            this.deadlineSeconds = clock.getTime() + timeoutSeconds;
            this.predicate = predicate;
        }

        public TimedPredicate(BooleanSupplier predicate, double timeoutSeconds) {
            this(RobotProvider.instance.getClock(), predicate, timeoutSeconds);
        }

        public boolean getAsBoolean() {
            if (predicate.getAsBoolean()) {
                succeeded = true;
                return true;
            }
            if (clock.getTime() >= deadlineSeconds) {
                succeeded = false;
                return true;
            } else {
                return false;
            }
        }

        public boolean succeeded() {
            return succeeded;
        }
    }

    private static ContextImpl<?> c_currentContext = null;

    /**
     * Returns the currently-executing Context.
     *
     * This is maintained for things like checking Mechanism ownership, but
     * intentionally only has package-private visibility - code outside of the
     * framework should ideally pass around references to the current context
     * object rather than cheating with this static accessor.
     */
    static ContextImpl<?> currentContext() {
        return c_currentContext;
    }

    /**
     * The top-level procedure being run by this Context.
     */
    private final RunnableWithContextWithValue<T> m_func;

    /**
     * If this Context was created by another context using
     * {@link #startAsync}, this will contain a reference to that originating
     * Context.
     */
    private final ContextImpl<?> m_parentContext;

    /**
     * The OS thread that this Context is executing on.
     */
    private Thread m_thread;

    /**
     * Used to synchronize access to this Context's state variable.
     */
    private final Object m_threadSync;

    /**
     * This Context's lifetime state.
     */
    private State m_state;

    /**
     * If one of the wait* methods has been called on this Context, this
     * contains the predicate which should be checked to determine whether
     * the Context's execution should be resumed. This makes it more efficient
     * to poll completion criteria without needing to context-switch between
     * threads.
     */
    private BooleanSupplier m_blockingPredicate;

    /**
     * Set to SUBROUTINE when this Context is executing and MAIN_THREAD
     * otherwise.
     */
    private ControlOwner m_controlOwner;

    /**
     * Contains the method name and line number at which this Context most
     * recently yielded.
     */
    private String m_previousWaitPoint;

    /**
     * The mechanisms that have been claimed by this Context using
     * takeOwnership. These will be automatically released when the Context
     * finishes executing.
     */
    private Set<Mechanism> m_ownedMechanisms = new HashSet<Mechanism>();

    /*
     * Constructors are intentionally private or package-private. New contexts
     * should be created with {@link Context#startAsync} or
     * {@link Scheduler#startAsync}.
     */

    ContextImpl(final RunnableWithContextWithValue<T> func, final ContextImpl<?> parentContext) {
        m_func = func;
        m_parentContext = parentContext;
        Logger.get(Category.FRAMEWORK)
                .logRaw(
                        Severity.DEBUG,
                        "Starting context " + getContextName() + " for " + func.toString());
        m_threadSync = new Object();
        m_previousWaitPoint = null;
        m_controlOwner = ControlOwner.MAIN_THREAD;
        m_state = State.NEW;
        setName(getContextName());
    }

    ContextImpl(final RunnableWithContextWithValue<T> func) {
        this(func, null);
    }

    ContextImpl(final RunnableWithContext func, final ContextImpl<?> parentContext) {
        this((ContextWithValue<T> context) -> func.run(context), parentContext);
    }

    ContextImpl(final RunnableWithContext func) {
        this(func, null);
    }

    /**
     * Returns a string meant to uniquely identify this Context (e.g. for use in logging).
     */
    public String getContextName() {
        return "Context/" + Integer.toHexString(hashCode()) + "/" + m_func.toString();
    }

    @Override
    public String toString() {
        String repr = getContextName();
        if (currentContext() == this) {
            repr += " running";
        }
        return repr;
    }

    public String getStackTrace() {
        if (m_thread != null) {
            return StackTraceUtils.getStackTrace(m_thread);
        } else {
            return "";
        }
    }

    /**
     * Walks up the call stack until it reaches a frame that isn't from the Context class, then
     * returns a string representation of that frame. This is used to generate a concise string
     * representation of from where the user called into framework code.
     */
    private String getExecutionPoint() {
        StackWalker walker = StackWalker.getInstance();
        return walker.walk(
                s ->
                        s.dropWhile(f -> f.getClassName() != ContextImpl.this.getClass().getName())
                                .filter(
                                        f ->
                                                f.getClassName()
                                                        != ContextImpl.this.getClass().getName())
                                .findFirst()
                                .map(StackFrame::toString)
                                .orElse(null));
    }

    /**
     * Wait until the baton (see the class comments) has been passed to this thread.
     *
     * @param thisOwner the thread from which this function is being called (and thus the
     *        baton-passing state that should be waited for)
     * @throws ContextStoppedException if cancel() is called on this Context while waiting.
     */
    private void waitForControl(final ControlOwner thisOwner) {
        // If this is being called from the worker thread, log from where in the
        // user's code that the context is waiting. This is provided as a
        // convenience so the user can track the progress of execution through
        // their procedures.
        if (thisOwner == ControlOwner.SUBROUTINE) {
            String waitPointTrace = getExecutionPoint();
            if (waitPointTrace != null && !waitPointTrace.equals(m_previousWaitPoint)) {
                Logger.get(Category.FRAMEWORK)
                        .logRaw(
                                Severity.DEBUG,
                                getContextName() + " is waiting at " + waitPointTrace);
                m_previousWaitPoint = waitPointTrace;
            }
        }
        // Wait for the baton to be passed to us.
        synchronized (m_threadSync) {
            while (m_controlOwner != thisOwner && m_state != State.DONE) {
                try {
                    m_threadSync.wait();
                } catch (InterruptedException e) {
                }
            }
            m_controlOwner = thisOwner;
            if (m_state != State.RUNNING && m_controlOwner == ControlOwner.SUBROUTINE) {
                throw new ContextStoppedException();
            }
        }
    }

    /**
     * Pass the baton (see the class comments) to the other thread and then wait for it to be passed
     * back.
     *
     * @param thisOwner the thread from which this function is being called (and thus the
     *        baton-passing state that should be waited for)
     * @param desiredOwner the thread to which the baton should be passed
     * @throws ContextStoppedException if cancel() is called on this Context while waiting.
     */
    private void transferControl(final ControlOwner thisOwner, final ControlOwner desiredOwner) {
        synchronized (m_threadSync) {
            // Make sure we currently have the baton before trying to give it to
            // someone else.
            if (m_controlOwner != thisOwner) {
                throw new IllegalStateException(
                        "Subroutine had control owner "
                                + m_controlOwner
                                + " but assumed control owner "
                                + thisOwner);
            }
            // Pass the baton.
            m_controlOwner = desiredOwner;
            if (m_controlOwner == ControlOwner.SUBROUTINE) {
                c_currentContext = this;
            } else {
                c_currentContext = null;
            }
            m_threadSync.notifyAll();
            // Wait for the baton to be passed back.
            waitForControl(thisOwner);
        }
    }

    /**
     * This is the entry point for this Context's worker thread.
     */
    private void threadFunction() {
        try {
            // OS threads run independently of one another, so we need to wait until
            // the baton is passed to us before we can start running the user's code
            waitForControl(ControlOwner.SUBROUTINE);
            // Call into the user's code.
            m_func.run(this);
            Logger.get(Category.FRAMEWORK)
                    .logRaw(Severity.DEBUG, "Context " + getContextName() + " finished");
        } catch (ContextStoppedException ex) {
            Logger.get(Category.FRAMEWORK)
                    .logRaw(Severity.WARNING, getContextName() + " was stopped");
        } catch (Exception ex) {
            ex.printStackTrace();
            LoggerExceptionUtils.logException(ex);
            Logger.get(Category.FRAMEWORK)
                    .logRaw(Severity.WARNING, "Context " + getContextName() + " died");
        } finally {
            for (Mechanism m : m_ownedMechanisms) {
                // Don't use this.releaseOwnership here, because that would cause a
                // ConcurrentModificationException since we're iterating over m_ownedMechanisms
                try {
                    m.releaseOwnership(this);
                } catch (Exception ex) {
                    LoggerExceptionUtils.logException(ex);
                }
            }
            synchronized (m_threadSync) {
                m_state = State.DONE;
                c_currentContext = null;
                m_threadSync.notifyAll();
            }
            m_ownedMechanisms.clear();
        }
    }

    @Override
    public boolean waitForConditionOrTimeout(
            final BooleanSupplier predicate, double timeoutSeconds) {
        TimedPredicate timedPredicate = new TimedPredicate(predicate, timeoutSeconds);
        waitFor(timedPredicate);
        return timedPredicate.succeeded();
    }

    @Override
    public void waitFor(final BooleanSupplier predicate) {
        if (!predicate.getAsBoolean()) {
            m_blockingPredicate = predicate;
            transferControl(ControlOwner.SUBROUTINE, ControlOwner.MAIN_THREAD);
        }
    }

    @Override
    public void waitFor(final LaunchedContext otherContext) {
        waitFor(otherContext::isDone);
    }

    @Override
    public void waitFor(final LaunchedContext... otherContexts) {
        waitFor(() -> Arrays.stream(otherContexts).allMatch(LaunchedContext::isDone));
    }

    @Override
    public void waitForSeconds(final double seconds) {
        double startTime = RobotProvider.instance.getClock().getTime();
        waitFor(() -> RobotProvider.instance.getClock().getTime() - startTime > seconds);
    }

    @Override
    public void yield() {
        m_blockingPredicate = null;
        transferControl(ControlOwner.SUBROUTINE, ControlOwner.MAIN_THREAD);
    }

    @Override
    public void yield(final T valueToYield) {
        m_lastYieldedValue = Optional.of(valueToYield);
        this.yield();
    }

    @Override
    public T lastYieldedValue() {
        return m_lastYieldedValue.orElse(null);
    }

    @Override
    public boolean hasYieldedValue() {
        return m_lastYieldedValue.isPresent();
    }

    @Override
    public T getAndClearLastYieldedValue() {
        final var result = m_lastYieldedValue;
        m_lastYieldedValue = Optional.empty();
        return result.orElse(null);
    }

    @Override
    public LaunchedContext startAsync(final RunnableWithContext func) {
        var newContext = new ContextImpl<>(func, this);
        newContext.schedule();
        return newContext;
    }

    @Override
    public <U> LaunchedContextWithValue<U> startAsync(final RunnableWithContextWithValue<U> func) {
        var newContext = new ContextImpl<U>(func, this);
        newContext.schedule();
        return newContext;
    }

    @Override
    public void runSync(final RunnableWithContext func) {
        func.run(this);
    }

    @Override
    public void initialize() {
        m_state = State.RUNNING;
        m_thread = new Thread(this::threadFunction, getContextName());
        m_thread.start();
    }

    @Override
    public boolean isFinished() {
        return isDone();
    }

    @Override
    public void end(boolean interrupted) {
        synchronized (m_threadSync) {
            if (m_state != State.DONE) {
                Logger.get(Category.FRAMEWORK)
                        .logRaw(Severity.DEBUG, "Stopping requested of " + getContextName());
                m_state = State.CANCELED;
                if (m_controlOwner == ControlOwner.SUBROUTINE) {
                    throw new IllegalStateException(
                            "A Procedure should not cancel() its own Context");
                }
                transferControl(ControlOwner.MAIN_THREAD, ControlOwner.SUBROUTINE);
                if (m_state != State.DONE) {
                    Logger.get(Category.FRAMEWORK)
                            .logRaw(
                                    Severity.ERROR,
                                    getContextName() + " did not end when requested");
                }
            }
        }
    }

    @Override
    public void execute() {
        if (m_state == State.DONE) {
            return;
        }
        if (m_blockingPredicate == null || m_blockingPredicate.getAsBoolean()) {
            transferControl(ControlOwner.MAIN_THREAD, ControlOwner.SUBROUTINE);
        }
    }

    @Override
    public boolean runsWhenDisabled() {
        // Maintains backward compatibility with the behavior of the old Maroon Framework scheduler.
        // TODO: Re-evaluate whether this should use the default Command behavior (return false).
        return true;
    }

    @Override
    public boolean isDone() {
        return m_state == State.DONE;
    }

    @Override
    public void takeOwnership(final Mechanism mechanism) {
        mechanism.takeOwnership(this, m_parentContext);
        m_ownedMechanisms.add(mechanism);
    }

    @Override
    public void releaseOwnership(final Mechanism mechanism) {
        mechanism.releaseOwnership(this);
        m_ownedMechanisms.remove(mechanism);
    }
}
