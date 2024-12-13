package com.team766.framework3;

import java.util.Objects;

/**
 * A Superstructure coordinates the behavior of several Mechanisms which could interfere with one
 * another if not commanded properly (e.g. they could collide with each other, or some other part
 * of the robot, or the floor, etc).
 *
 * A Mechanism in a Superstructure cannot be reserved individually by Procedures (Procedures
 * should reserve the entire Superstructure) and cannot have an Idle request. Only the
 * Superstructure should set requests on its constituent Mechanisms (in its
 * {@link #run(R, boolean)} method).
 */
public abstract class Superstructure<R extends Request, S extends Record & Status>
        extends Mechanism<R, S> {
    private static void setSuperstructure(Superstructure<?, ?> s, Mechanism<?, ?> m) {
        Objects.requireNonNull(m);
        if (m.superstructure != null) {
            throw new IllegalStateException("Mechanism is already part of a superstructure");
        }
        if (m.getIdleRequest() != null) {
            throw new UnsupportedOperationException(
                    "A Mechanism contained in a superstructure cannot define an idle request. "
                            + "Use the superstructure's idle request to control the idle behavior "
                            + "of the contained Mechanisms.");
        }
        m.superstructure = s;
    }

    public Superstructure(Mechanism<?, ?> submechanism, Mechanism<?, ?>... submechanisms) {
        setSuperstructure(this, submechanism);
        for (var m : submechanisms) {
            setSuperstructure(this, m);
        }
    }
}
