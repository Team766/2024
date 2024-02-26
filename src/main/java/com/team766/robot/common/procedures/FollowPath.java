package com.team766.robot.common.procedures;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.common.constants.ConfigConstants;
import com.team766.robot.common.constants.PathPlannerConstants;
import com.team766.robot.common.mechanisms.Drive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;

public class FollowPath extends Procedure {
    private Drive drive;
    private PathPlannerPath path;
    private PathPlannerTrajectory generatedTrajectory;
    private final ReplanningConfig replanningConfig;
    private final PPHolonomicDriveController controller;
    private final Timer timer = new Timer();

    //     public FollowPath(
    //             PathPlannerPath path,
    //             ReplanningConfig replanningConfig,
    //             PPHolonomicDriveController controller
    //             /* TODO: add flip path support */ ) {
    //         this.path = path;
    //         this.replanningConfig = replanningConfig;
    //         this.controller = controller;
    //     }

    public FollowPath(PathPlannerPath path, ReplanningConfig replanningConfig, Drive drive) {
        this.path = path;
        this.replanningConfig = replanningConfig;
        this.drive = drive;
        double maxSpeed =
                ConfigFileReader.getInstance()
                        .getDouble(ConfigConstants.PATH_FOLLOWING_MAX_MODULE_SPEED_MPS)
                        .valueOr(PathPlannerConstants.MAX_SPEED_MPS);

        // log("max speed: " + maxSpeed);

        double translationP =
                ConfigFileReader.getInstance()
                        .getDouble(ConfigConstants.PATH_FOLLOWING_TRANSLATION_P)
                        .valueOr(PathPlannerConstants.TRANSLATION_P);
        double translationI =
                ConfigFileReader.getInstance()
                        .getDouble(ConfigConstants.PATH_FOLLOWING_TRANSLATION_I)
                        .valueOr(PathPlannerConstants.TRANSLATION_I);
        double translationD =
                ConfigFileReader.getInstance()
                        .getDouble(ConfigConstants.PATH_FOLLOWING_TRANSLATION_D)
                        .valueOr(PathPlannerConstants.TRANSLATION_D);
        double rotationP =
                ConfigFileReader.getInstance()
                        .getDouble(ConfigConstants.PATH_FOLLOWING_ROTATION_P)
                        .valueOr(PathPlannerConstants.ROTATION_P);
        double rotationI =
                ConfigFileReader.getInstance()
                        .getDouble(ConfigConstants.PATH_FOLLOWING_ROTATION_I)
                        .valueOr(PathPlannerConstants.ROTATION_I);
        double rotationD =
                ConfigFileReader.getInstance()
                        .getDouble(ConfigConstants.PATH_FOLLOWING_ROTATION_D)
                        .valueOr(PathPlannerConstants.ROTATION_D);

        controller =
                new PPHolonomicDriveController(
                        new PIDConstants(translationP, translationI, translationD),
                        new PIDConstants(rotationP, rotationI, rotationD),
                        maxSpeed,
                        drive.maxWheelDistToCenter());
    }

    public FollowPath(String autoName, Drive drive) {
        this(PathPlannerPath.fromPathFile(autoName), PathPlannerConstants.REPLANNING_CONFIG, drive);
    }

    @Override
    public void run(Context context) {
        context.takeOwnership(drive);

        // intitialization

        // TODO: flip path as necessary
        Pose2d curPose = drive.getCurrentPosition();
        ChassisSpeeds currentSpeeds = drive.getChassisSpeeds();
        // log("Autoning");
        // log("current pose in fp: " + curPose);

        controller.reset(curPose, currentSpeeds);

        if (replanningConfig.enableInitialReplanning
                && curPose.getTranslation().getDistance(path.getPoint(0).position) > 0.25) {
            replanPath(curPose, currentSpeeds);
            //    log("replanned path");
        } else {
            generatedTrajectory = path.getTrajectory(currentSpeeds, curPose.getRotation());
            //    log("generated normal trajectory");
        }

        timer.reset();
        timer.start();

        // execute
        log("time: " + generatedTrajectory.getTotalTimeSeconds());
        while (!timer.hasElapsed(generatedTrajectory.getTotalTimeSeconds())) {
            double currentTime = timer.get();
            // log("current time: " + currentTime);
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
            //     log("curPose: " + curPose);
            //     log("cur rot vel: " + currentSpeeds.omegaRadiansPerSecond);
            //     log("targetState: " + targetState.getTargetHolonomicPose());
            //     log("intended ang vel rps: " + targetState.headingAngularVelocityRps);

            //     log("targetSpeeds: " + targetSpeeds);
            org.littletonrobotics.junction.Logger.recordOutput(
                    "current heading", curPose.getRotation().getRadians());

            org.littletonrobotics.junction.Logger.recordOutput(
                    "input rotational velocity", targetSpeeds.omegaRadiansPerSecond);
            org.littletonrobotics.junction.Logger.recordOutput("curPose", curPose);
            org.littletonrobotics.junction.Logger.recordOutput(
                    "targetState", targetState.getTargetHolonomicPose());
            drive.controlRobotOriented(targetSpeeds);
            context.yield();
        }

        if (path.getGoalEndState().getVelocity() < 0.1) {
            drive.stopDrive();
        }
    }

    private void replanPath(Pose2d currentPose, ChassisSpeeds currentSpeeds) {
        PathPlannerPath replanned = path.replan(currentPose, currentSpeeds);
        generatedTrajectory =
                new PathPlannerTrajectory(replanned, currentSpeeds, currentPose.getRotation());
    }
}
