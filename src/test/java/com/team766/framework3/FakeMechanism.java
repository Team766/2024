package com.team766.framework3;

import static com.team766.framework3.Conditions.checkForStatusWith;

class FakeMechanism extends Mechanism<FakeMechanism.FakeRequest, FakeMechanism.FakeStatus> {
    public record FakeStatus(int currentState) implements Status {}

    public record FakeRequest(int targetState) implements Request {
        @Override
        public boolean isDone() {
            return checkForStatusWith(FakeStatus.class, s -> s.currentState() == targetState);
        }
    }

    FakeRequest currentRequest;
    Boolean wasRequestNew = null;

    @Override
    protected FakeRequest getInitialRequest() {
        return new FakeRequest(-1);
    }

    @Override
    protected FakeStatus run(FakeRequest request, boolean isRequestNew) {
        currentRequest = request;
        wasRequestNew = isRequestNew;
        return new FakeStatus(request.targetState());
    }
}

class FakeMechanism1 extends FakeMechanism {}

class FakeMechanism2 extends FakeMechanism {}

class FakeMechanism3 extends FakeMechanism {}
