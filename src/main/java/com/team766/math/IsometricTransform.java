package com.team766.math;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class IsometricTransform {
    public final Rotation rotation;
    public final Vector3D translation;

    public static final IsometricTransform IDENTITY =
            new IsometricTransform(Rotation.IDENTITY, Vector3D.ZERO);

    public IsometricTransform(final Rotation rotation_, final Vector3D translation_) {
        this.rotation = rotation_;
        this.translation = translation_;
    }

    Vector3D applyInverseTo(final Vector3D u) {
        return rotation.applyInverseTo(u.subtract(translation));
    }

    Vector3D applyTo(final Vector3D u) {
        return rotation.applyTo(u).add(translation);
    }

    IsometricTransform compose(final IsometricTransform other) {
        return new IsometricTransform(
                rotation.compose(other.rotation, RotationConvention.VECTOR_OPERATOR),
                rotation.applyTo(other.translation).add(translation));
    }

    IsometricTransform composeInverse(final IsometricTransform other) {
        return new IsometricTransform(
                rotation.composeInverse(other.rotation, RotationConvention.VECTOR_OPERATOR),
                rotation.applyInverseTo(other.translation).subtract(translation));
    }

    IsometricTransform invert() {
        return new IsometricTransform(rotation.revert(), rotation.applyInverseTo(translation));
    }
}
