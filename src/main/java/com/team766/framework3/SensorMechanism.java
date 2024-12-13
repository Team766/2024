package com.team766.framework3;

/**
 * A SensorMechanism is a Mechanism which only produces data; it can't be commanded by a Procedure
 * or RuleEngine. This is useful for writing code for cameras and other sensors.
 */
public abstract class SensorMechanism<S extends Record & Status>
        extends Mechanism<SensorMechanism.EmptyRequest, S> {
    protected record EmptyRequest() implements Request {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    @Override
    protected final EmptyRequest getInitialRequest() {
        return new EmptyRequest();
    }

    @Override
    protected final S run(EmptyRequest request, boolean isRequestNew) {
        return run();
    }

    protected abstract S run();
}
