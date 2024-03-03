package com.team766.math;

import java.util.function.Function;

public class Math {
    public static double clamp(final double x, final double min, final double max) {
        if (x < min) {
            return min;
        }
        if (x > max) {
            return max;
        }
        return x;
    }

    /**
     * Returns the given angle, normalized to be within the range [-180, 180)
     */
    public static double normalizeAngleDegrees(double angle) {
        while (angle < -180) {
            angle += 360;
        }
        while (angle >= 180) {
            angle -= 360;
        }
        return angle;
    }

    public static <T> double interpolate(
            T[] data, double targetX, Function<T, Double> xGetter, Function<T, Double> yGetter) {
        if (data.length == 0) {
            throw new IndexOutOfBoundsException("No data to interpolate!");
        }
        // data must be sorted, lowest to highest by x
        if (targetX <= xGetter.apply(data[0])) {
            // if our target x is below / at the beginning of the data, return the first y
            return yGetter.apply(data[0]);
        } else if (targetX >= xGetter.apply(data[data.length - 1])) {
            // if our target x is at / beyond the end of the data, return the final y
            return yGetter.apply(data[data.length - 1]);
        }

        // search for the target x in the data range
        int index = 0;
        for (int i = 0; i < data.length; ++i) {
            double x = xGetter.apply(data[i]);

            // exact match!
            // if (targetX == x) {
            //     return yGetter.apply(data[i]);
            // }

            // found where our target x fits in our data range
            if (targetX < x) {
                index = i;
                break;
            }
        }

        // interpolate
        double x0 = xGetter.apply(data[index - 1]);
        double x1 = xGetter.apply(data[index]);
        double y0 = yGetter.apply(data[index - 1]);
        double y1 = yGetter.apply(data[index]);

        double slope = (y1 - y0) / (x1 - x0);
        return y0 + (targetX - x0) * slope;
    }
}
