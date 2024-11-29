package com.team766.ViSIONbase;

public class ScoringPosition {
    public final double speed, angle, x_position, y_position, swerve_angle;
    public final int tagId;

    /*
     * @param speed the speed to set the shooter, in motor shaft rps.
     * @param angle the angle to set the shooter
     * @param x_position the x_position of the robot relative to the AprilTag
     * @param y_position the y_position of the robot relative to the AprilTag
     * @param swerve_angle the angle the robot should be facing relative to the AprilTag (where 0 degrees is flush)
     */
    public ScoringPosition(
            double speed,
            double angle,
            double x_position,
            double y_position,
            double swerve_angle,
            int tagId) {
        this.speed = speed;
        this.angle = angle;
        this.x_position = x_position;
        this.y_position = y_position;
        this.swerve_angle = swerve_angle;
        this.tagId = tagId;
    }
}
