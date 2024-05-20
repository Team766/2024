package com.team766.framework.conditions;

import java.util.LinkedList;
import java.util.List;

public final class RuleEngine {
    private final List<Runnable> startFrameCallbacks = new LinkedList<>();

    /* package */ void registerStartFrameCallback(Runnable callback) {
        startFrameCallbacks.add(callback);
    }

    public void startFrame() {
        for (var callback : startFrameCallbacks) {
            callback.run();
        }
    }

    public void endFrame() {}
}
