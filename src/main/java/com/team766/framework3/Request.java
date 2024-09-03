package com.team766.framework3;

/**
 * Code (typically in a Procedure) that manipulates Mechanisms can simply make {@link Request}s of the
 * Mechanism, specifying the desired action and any parameters (eg, spin up a shooter to a specific speed),
 * without worrying about any of the internals of the Mechanism.  The calling Code can then check if the
 * Request has been fulfilled by querying the {@link Status} published by the Mechanism.  For convenience,
 * the Request can let the caller know when it has been fulfilled via the {@link #isDone} method.
 *
 * Each Mechanism will have its own implementation of the {@link Request} marker interface.
 */
public interface Request {

    /**
     * Checks whether or not this request has been fulfilled, via the supplied {@link Status}.  This
     * {@link Status} should be the latest one retrieved via {@link StatusBus#getStatus(Class)}.
     */
    boolean isDone(Status status);

    // TODO: do we need any way of checking if the request has been bumped/canceled?
}
