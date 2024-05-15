package com.team766.framework;

import static org.junit.jupiter.api.Assertions.*;

import com.team766.framework.conditions.RuleEngine;
import com.team766.framework.conditions.RuleEngineProvider;
import org.junit.jupiter.api.Test;

public class OIFragmentTest {

    private static class TestFragment extends OIFragment {
        public TestFragment(RuleEngineProvider oi) {
            super(oi);
        }

        @Override
        protected void dispatch() {}
    }

    @Test
    public void testDefaultName() {
        assertEquals("TestFragment", new TestFragment(() -> new RuleEngine()).getName());
    }
}
