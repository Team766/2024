package com.team766.framework3;

class FakeMechanism extends Mechanism<FakeMechanism.FakeRequest> {
    record FakeStatus(int currentState) implements Status {}

    public record FakeRequest(int targetState) implements Request<FakeStatus> {
        @Override
        public boolean isDone(FakeStatus status) {
            return status.currentState() == targetState;
        }
    }

    FakeRequest currentRequest;
    Boolean wasRequestNew = null;

    @Override
    protected FakeRequest getInitialRequest() {
        return new FakeRequest(-1);
    }

    @Override
    protected void run(FakeRequest request, boolean isRequestNew) {
        currentRequest = request;
        wasRequestNew = isRequestNew;
    }
}

class FakeMechanism1 extends FakeMechanism {}

class FakeMechanism2 extends FakeMechanism {}

class FakeMechanism3 extends FakeMechanism {}
