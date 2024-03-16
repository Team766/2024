package com.team766.robot.common.procedures;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.ReplanningConfig;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.common.constants.PathPlannerConstants;
import com.team766.robot.common.mechanisms.Drive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;

public class FollowPath extends Procedure {
    private final Drive drive;
    private final PathPlannerPath path;
    private final ReplanningConfig replanningConfig;
    private final PPHolonomicDriveController controller;
    private final Timer timer = new Timer();
    private PathPlannerTrajectory generatedTrajectory;

    public FollowPath(
            PathPlannerPath path,
            ReplanningConfig replanningConfig,
            PPHolonomicDriveController controller,
            Drive drive,
            boolean shouldFlipPath) {
        this.path = shouldFlipPath ? path.flipPath() : path;
        this.replanningConfig = replanningConfig;
        this.controller = controller;
        this.drive = drive;
    }

    public FollowPath(
            String autoName,
            PPHolonomicDriveController controller,
            Drive drive,
            boolean shouldFlipPath) {
        this(
                PathPlannerPath.fromPathFile(autoName),
                PathPlannerConstants.REPLANNING_CONFIG,
                controller,
                drive,
                shouldFlipPath);
    }

    @Override
    public void run(Context context) {
        context.takeOwnership(drive);

        // intitialization

        Pose2d curPose = drive.getCurrentPosition();
        ChassisSpeeds currentSpeeds = drive.getChassisSpeeds();

        controller.reset(curPose, currentSpeeds);

        if (replanningConfig.enableInitialReplanning
                && curPose.getTranslation().getDistance(path.getPoint(0).position) > 0.25) {
            replanPath(curPose, currentSpeeds);
        } else {
            generatedTrajectory = path.getTrajectory(currentSpeeds, curPose.getRotation());
        }

        timer.reset();
        timer.start();

        // execute
        log("time: " + generatedTrajectory.getTotalTimeSeconds());
        while (!timer.hasElapsed(generatedTrajectory.getTotalTimeSeconds())) {
            double currentTime = timer.get();
            PathPlannerTrajectory.State targetState = generatedTrajectory.sample(currentTime);
            curPose = drive.getCurrentPosition();
            currentSpeeds = drive.getChassisSpeeds();

            if (replanningConfig.enableDynamicReplanning) {
                // TODO: why abs?
                double previousError = Math.abs(controller.getPositionalError());
                double currentError =
                        curPose.getTranslation().getDistance(targetState.positionMeters);

                if (currentError >= replanningConfig.dynamicReplanningTotalErrorThreshold
                        || currentError - previousError
                                // TODO: is this always negative?
                                >= replanningConfig.dynamicReplanningErrorSpikeThreshold) {
                    replanPath(curPose, currentSpeeds);
                    timer.reset();
                    targetState = generatedTrajectory.sample(0);
                }
            }

            ChassisSpeeds targetSpeeds =
                    controller.calculateRobotRelativeSpeeds(curPose, targetState);

            org.littletonrobotics.junction.Logger.recordOutput(
                    "current heading", curPose.getRotation().getRadians());

            org.littletonrobotics.junction.Logger.recordOutput(
                    "input rotational velocity", targetSpeeds.omegaRadiansPerSecond);
            org.littletonrobotics.junction.Logger.recordOutput(
                    "targetState", targetState.getTargetHolonomicPose());
            drive.controlRobotOriented(targetSpeeds);
            context.yield();
        }

        if (path.getGoalEndState().getVelocity() < 0.1) {
            drive.stopDrive();
            drive.setCross();
        }
    }

    private void replanPath(Pose2d currentPose, ChassisSpeeds currentSpeeds) {
        PathPlannerPath replanned = path.replan(currentPose, currentSpeeds);
        generatedTrajectory =
                new PathPlannerTrajectory(replanned, currentSpeeds, currentPose.getRotation());
    }
}
