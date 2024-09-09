package com.team766.framework3;

import com.team766.framework.LaunchedContext;
import java.util.function.BooleanSupplier;

/**
 * Context is the framework's representation of a single thread of execution.
 *
 * We may want to have multiple procedures running at the same time on a robot.
 * For example, the robot could be raising an arm mechanism while also driving.
 * Each of those procedures would have a separate Context. Each of those
 * procedures may call other procedures directly; those procedures would share
 * the same Context. Each Context can only be running a single procedure at a
 * time. If a procedure wants to call multiple other procedures at the same
 * time, it has to create new Contexts for them (using the {@link #startAsync}
 * method).
 *
 * Use the Context instance passed to your procedure whenever you want your
 * procedure to wait for something. For example, to have your procedure pause
 * for a certain amount of time, call context.waitForSeconds. Multiple Contexts
 * run at the same time using cooperative multitasking, which means procedures
 * have to explicitly indicate when another Context should be allowed to run.
 * Using Context's wait* methods will allow other Contexts to run while this one
 * is waiting. If your procedure will run for a while without needing to wait
 * (this often happens if your procedure has a while loop), then it should
 * periodically call context.yield() (for example, at the start of each
 * iteration of the while loop) to still allow other Contexts to run.
 *
 * This cooperative multitasking paradigm is used by the framework to ensure
 * that only one Context is actually running at a time, which allows us to avoid
 * needing to deal with concurrency issues like data race conditions. Even
 * though only one Context is running at once, it's still incredibly helpful to
 * express the code using this separate-threads-of-execution paradigm, as it
 * allows each procedure to be written in procedural style
 * (https://en.wikipedia.org/wiki/Procedural_programming "procedural languages
 * model execution of the program as a sequence of imperative commands"), rather
 * than as state machines or in continuation-passing style, which can be much
 * more complicated to reason about, especially for new programmers.
 */
public interface Context {
    /**
     * Pauses the execution of this Context until the given predicate returns true or until
     * the timeout has elapsed.  Yields to other Contexts in the meantime.
     *
     * Note that the predicate will be evaluated repeatedly (possibly on a different thread) while
     * the Context is paused to determine whether it should continue waiting.
     *
     * @return True if the predicate succeeded, false if the wait timed out.
     */
    boolean waitForConditionOrTimeout(final BooleanSupplier predicate, double timeoutSeconds);

    /**
     * Pauses the execution of this Context until the given predicate returns true. Yields to other
     * Contexts in the meantime.
     *
     * Note that the predicate will be evaluated repeatedly (possibly on a different thread) while
     * the Context is paused to determine whether it should continue waiting.
     */
    void waitFor(final BooleanSupplier predicate);

    /**
     * Pauses the execution of this Context until the given LaunchedContext has finished running.
     */
    void waitFor(final LaunchedContext otherContext);

    /**
     * Pauses the execution of this Context until all of the given LaunchedContexts have finished
     * running.
     */
    void waitFor(final LaunchedContext... otherContexts);

    /**
     * Momentarily pause execution of this Context to allow other Contexts to execute. Execution of
     * this Context will resume as soon as possible after the other Contexts have been given a
     * chance to run.
     *
     * Procedures should call this periodically if they wouldn't otherwise call one of the wait*
     * methods for a while.
     */
    void yield();

    /**
     * Pauses the execution of this Context for the given length of time.
     */
    void waitForSeconds(final double seconds);

    /**
     * Start running a new Context so the given procedure can run in parallel.
     */
    LaunchedContext startAsync(final RunnableWithContext func);

    /**
     * Run the given Procedure synchronously (the calling Procedure will not resume until this one
     * has finished).
     */
    void runSync(final RunnableWithContext func);
}
