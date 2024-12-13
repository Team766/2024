package com.team766.framework3;

import java.util.ArrayList;
import java.util.Objects;

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
