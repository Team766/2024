package com.team766.robot.gatorade.procedures;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.gatorade.Robot;
import com.team766.robot.gatorade.constants.ConfigConstants;
import com.team766.robot.gatorade.constants.OdometryInputConstants;
import com.team766.robot.gatorade.constants.PathPlannerConstants;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;

public class FollowPath extends Procedure {
    private PathPlannerPath path;
    private PathPlannerTrajectory generatedTrajectory;
    private final ReplanningConfig replanningConfig;
    private final PPHolonomicDriveController controller;
    private final Timer timer = new Timer();

    public FollowPath(
            PathPlannerPath path,
            ReplanningConfig replanningConfig,
            PPHolonomicDriveController controller
            /* TODO: add flip path support */ ) {
        this.path = path;
        this.replanningConfig = replanningConfig;
        this.controller = controller;
    }

    public FollowPath(PathPlannerPath path, ReplanningConfig replanningConfig) {
        this.path = path;
        this.replanningConfig = replanningConfig;
        double maxSpeed =
                ConfigFileReader.getInstance()
                        .getDouble(ConfigConstants.PATH_FOLLOWING_MAX_MODULE_SPEED_MPS)
                        .valueOr(PathPlannerConstants.MAX_SPEED_MPS);

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
                        OdometryInputConstants.DISTANCE_BETWEEN_WHEELS
                                * Math.sqrt(2)
                                / 2 // calculating distance between center of robot and wheels
                        );
    }

    public FollowPath(String autoName) {
        this(PathPlannerPath.fromPathFile(autoName), PathPlannerConstants.REPLANNING_CONFIG);
    }

    @Override
    public void run(Context context) {
        context.takeOwnership(Robot.drive);

        // intitialization

        // TODO: flip path as necessary
        Pose2d curPose = Robot.drive.getCurrentPosition();
        ChassisSpeeds currentSpeeds = Robot.drive.getChassisSpeeds();

        controller.reset(curPose, currentSpeeds);

        if (curPose.getTranslation().getDistance(path.getPoint(0).position) > 0.25) {
            replanPath(curPose, currentSpeeds);
        } else {
            generatedTrajectory = path.getTrajectory(currentSpeeds, curPose.getRotation());
        }

        timer.reset();
        timer.start();

        // execute

        while (timer.hasElapsed(generatedTrajectory.getTotalTimeSeconds())) {
            double currentTime = timer.get();
            PathPlannerTrajectory.State targetState = generatedTrajectory.sample(currentTime);
            curPose = Robot.drive.getCurrentPosition();
            currentSpeeds = Robot.drive.getChassisSpeeds();

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

            Robot.drive.controlFieldOriented(targetSpeeds);
        }

        if (path.getGoalEndState().getVelocity() < 0.1) {
            Robot.drive.stopDrive();
        }
    }

    private void replanPath(Pose2d currentPose, ChassisSpeeds currentSpeeds) {
        PathPlannerPath replanned = path.replan(currentPose, currentSpeeds);
        generatedTrajectory =
                new PathPlannerTrajectory(replanned, currentSpeeds, currentPose.getRotation());
    }
}
