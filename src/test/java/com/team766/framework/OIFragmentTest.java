package com.team766.framework;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class OIFragmentTest {

    private static class TestFragment extends OIBase {
        @Override
        protected void dispatch() {}
    }

    @Test
    public void testDefaultName() {
        assertInstanceOf(OIFragment.class, new TestFragment());
        assertEquals("TestFragment", new TestFragment().getName());
    }
}
