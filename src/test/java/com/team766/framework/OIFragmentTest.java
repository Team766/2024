package com.team766.framework;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class OIFragmentTest {

    private static class TestFragment extends OIFragment {
        @Override
        protected void handleOI(Context context) {}
    }

    @Test
    public void testDefaultName() {
        assertEquals("TestFragment", new TestFragment().getName());
    }
}
