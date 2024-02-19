package com.team766.ViSIONbase;

public class ScoringPosition {
    public final double power, angle, x_position, y_position, swerve_angle;

    public ScoringPosition(
            double power, double angle, double x_position, double y_position, double swerve_angle) {
        this.power = power;
        this.angle = angle;
        this.x_position = x_position;
        this.y_position = y_position;
        this.swerve_angle = swerve_angle;
    }
}
