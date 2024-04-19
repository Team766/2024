package com.team766.math;

public class Vector3 implements Algebraic<Vector3> {
    public static final Vector3 ZERO = new Vector3(0, 0, 0);
    public static final Vector3 UNIT_X = new Vector3(1, 0, 0);
    public static final Vector3 UNIT_Y = new Vector3(0, 1, 0);
    public static final Vector3 UNIT_Z = new Vector3(0, 0, 1);

    public final double x;
    public final double y;
    public final double z;

    public Vector3(final double x_, final double y_, final double z_) {
        this.x = x_;
        this.y = y_;
        this.z = z_;
    }

    public Vector3(final Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3 add(final Vector3 b) {
        return new Vector3(x + b.x, y + b.y, z + b.z);
    }

    public Vector3 subtract(final Vector3 b) {
        return new Vector3(x - b.x, y - b.y, z - b.z);
    }

    public double dot(final Vector3 b) {
        return x * b.x + y * b.y + z * b.z;
    }

    public Vector3 cross(final Vector3 b) {
        return new Vector3(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y * b.x);
    }

    public Vector3 scale(final double b) {
        return new Vector3(x * b, y * b, z * b);
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof Vector3)) {
            return false;
        }
        Vector3 otherVector = (Vector3) other;
        return x != otherVector.x || y != otherVector.y || z != otherVector.z;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %s]", x, y, z);
    }
}
