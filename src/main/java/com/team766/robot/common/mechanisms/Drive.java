package com.team766.robot.common.mechanisms;

import com.team766.framework.Mechanism;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

public abstract class Drive extends Mechanism {
    /**
     * Controls the robot with a ChassisSpeeds input
     *
     * @param chassisSpeeds the velocity vector of the robot
     */
    public abstract void controlRobotOriented(ChassisSpeeds chassisSpeeds);

    /**
     * Stops the robot drive
     */
    public abstract void stopDrive();

    public abstract Pose2d getCurrentPosition();

    public abstract void resetCurrentPosition();

    public abstract ChassisSpeeds getChassisSpeeds();

    public abstract void setCross(); // dummy method
}