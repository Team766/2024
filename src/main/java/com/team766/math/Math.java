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

    public static double deadzone(double value, double deadzone) {
        return java.lang.Math.abs(value) >= deadzone ? value : 0;
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

    /**
     * Performs simple linear interpolation (as described in
     * https://en.wikipedia.org/wiki/Linear_interpolation) of data in an array of type T.
     * NOTE: the data array must be sorted by x, from lowest to highest.
     *
     * The x values (eg measured data and target data point) should be available via a getter in T.
     * The y values (what this interpolates from measured data) should be available via a getter in T.
     *
     * Example usage:
     * <pre>
     *   public record Data(double x, double y);
     *   ...
     *   Data[] data = new Data[] { new Data(0.0, 1.0), new Data(1.0, 32.0), ... };
     *   double interpolatedY = Math.interpolate(data, 0.5, Data::x, Data::y);
     * </pre>
     *
     * @param <T> The class containing the x and y data.
     * @param data An array of data points, sorted by x from lowest to highest.
     * @param targetX The target x value.
     * @param xGetter A getter function that takes a T and returns the x value for that data point.
     * @param yGetter A getter function that takes a T and returns the y value for that data point.
     * @return The interpolated y value.  Returns the lowest y in data if x is below/at the low end of the range.
     * Returns the highest y if x is above.
     */
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
        int index;
        double x1;

        for (index = 1; ; ++index) {
            x1 = xGetter.apply(data[index]);

            // found where our target x fits in our data range
            if (targetX < x1) {
                break;
            }
        }

        // interpolate
        final double x0 = xGetter.apply(data[index - 1]);
        final double y0 = yGetter.apply(data[index - 1]);
        final double y1 = yGetter.apply(data[index]);

        final double slope = (y1 - y0) / (x1 - x0);

        return y0 + (targetX - x0) * slope;
    }
}
