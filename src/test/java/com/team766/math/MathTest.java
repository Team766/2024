package com.team766.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MathTest {

    private record TestData(double x, double y) {}

    @Test
    public void testInterpolateEmpty() {
        TestData[] data = new TestData[] {};
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> Math.interpolate(data, 0, TestData::x, TestData::y));
    }

    @Test
    public void testInterpolateBelowRange() {
        TestData[] data = new TestData[] {
            new TestData(1.0, 0.0),
            new TestData(2.5, 25),
            new TestData(5.0, 50.0),
            new TestData(10.0, 100.0)
        };

        assertEquals(0.0, Math.interpolate(data, 0.0, TestData::x, TestData::y));
    }

    @Test
    public void testInterpolateAboveRange() {
        TestData[] data = new TestData[] {
            new TestData(0.0, 0.0),
            new TestData(2.5, 25),
            new TestData(5.0, 50.0),
            new TestData(10.0, 100.0)
        };

        assertEquals(100.0, Math.interpolate(data, 11.0, TestData::x, TestData::y));
    }

    @Test
    public void testInterpolateExactMatches() {
        TestData[] data = new TestData[] {
            new TestData(0.0, 0.0),
            new TestData(2.5, 25),
            new TestData(5.0, 50.0),
            new TestData(10.0, 100.0)
        };

        assertEquals(0.0, Math.interpolate(data, 0.0, TestData::x, TestData::y));
        assertEquals(25.0, Math.interpolate(data, 2.5, TestData::x, TestData::y));
        assertEquals(50.0, Math.interpolate(data, 5.0, TestData::x, TestData::y));
        assertEquals(100.0, Math.interpolate(data, 10.0, TestData::x, TestData::y));
    }

    @Test
    public void testInterpolateBetweenPoints() {
        TestData[] data = new TestData[] {
            new TestData(0.0, 0.0), new TestData(5.0, 50.0), new TestData(10.0, 100.0)
        };

        assertEquals(25.0, Math.interpolate(data, 2.5, TestData::x, TestData::y));
        assertEquals(30.0, Math.interpolate(data, 3.0, TestData::x, TestData::y));
        assertEquals(52.5, Math.interpolate(data, 5.25, TestData::x, TestData::y));
        assertEquals(75.0, Math.interpolate(data, 7.5, TestData::x, TestData::y));
    }
}
