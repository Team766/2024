package com.team766.framework;

import static org.junit.jupiter.api.Assertions.*;

import com.team766.TestCase;
import org.junit.jupiter.api.Test;

public class ContextTest extends TestCase {
    /// Regression test: calling stop() on a Context before it is allowed to
    /// run the first time should not crash the program.
    @Test
    public void testStopOnFirstTick() {
        var lc = Procedure.noOp();
        lc.schedule();
        lc.cancel();

        step();

        assertTrue(lc.isFinished());
    }
}
