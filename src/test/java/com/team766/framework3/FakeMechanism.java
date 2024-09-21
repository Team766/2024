package com.team766.framework3;

class FakeMechanism extends Mechanism<FakeMechanism.FakeRequest> {
    record FakeStatus(int currentState) implements Status {}

    public record FakeRequest(int targetState) implements Request<FakeStatus> {
        @Override
        public boolean isDone(FakeStatus status) {
            return status.currentState() == targetState;
        }
    }

    private FakeRequest currentRequest;

    public FakeRequest currentRequest() {
        return currentRequest;
    }

    @Override
    protected void run(FakeRequest request, boolean isRequestNew) {
        this.currentRequest = request;
    }
}

class FakeMechanism1 extends FakeMechanism {}

class FakeMechanism2 extends FakeMechanism {}

class FakeMechanism3 extends FakeMechanism {}
