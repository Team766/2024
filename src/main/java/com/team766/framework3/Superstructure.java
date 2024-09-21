package com.team766.framework3;

import java.util.ArrayList;
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
    private ArrayList<Mechanism<?, ?>> submechanisms = new ArrayList<>();

    @Override
    /* package */ void periodicInternal() {
        for (var m : submechanisms) {
            m.periodicInternal();
        }

        super.periodicInternal();
    }

    protected <M extends Mechanism<?, ?>> M addMechanism(M submechanism) {
        Objects.requireNonNull(submechanism);
        submechanism.setSuperstructure(this);
        submechanisms.add(submechanism);
        return submechanism;
    }
}
