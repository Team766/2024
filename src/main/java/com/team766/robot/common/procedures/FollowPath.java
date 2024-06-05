package com.team766.robot.common.procedures;

import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.ReplanningConfig;
import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.common.constants.PathPlannerConstants;
import com.team766.robot.common.mechanisms.Drive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Timer;
import java.util.Optional;

@CollectReservations
public class FollowPath extends MagicProcedure<FollowPath_Reservations> {
    @Reserve Drive drive;

    private PathPlannerPath path; // may be flipped
    private final ReplanningConfig replanningConfig;
    private final Timer timer = new Timer();
    private PathPlannerTrajectory generatedTrajectory;

    public FollowPath(PathPlannerPath path, ReplanningConfig replanningConfig) {
        this.path = path;
        this.replanningConfig = replanningConfig;
    }

    public FollowPath(String autoName) {
        this(PathPlannerPath.fromPathFile(autoName), PathPlannerConstants.REPLANNING_CONFIG);
    }

    @Override
    public void run(Context context) {
        Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            boolean flip = (alliance.get() == Alliance.Red);
            if (flip) {
                path = path.flipPath();
            }
        } else {
            log("Unable to get Alliance in FollowPath.");
            // TODO: don't follow this path?
        }

        // intitialization

        Pose2d curPose = drive.getStatus().currentPosition();
        ChassisSpeeds currentSpeeds = drive.getStatus().chassisSpeeds();

        drive.controller.reset(curPose, currentSpeeds);

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
            curPose = drive.getStatus().currentPosition();
            currentSpeeds = drive.getStatus().chassisSpeeds();

            if (replanningConfig.enableDynamicReplanning) {
                // TODO: why abs?
                double previousError = Math.abs(drive.controller.getPositionalError());
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
                    drive.controller.calculateRobotRelativeSpeeds(curPose, targetState);

            org.littletonrobotics.junction.Logger.recordOutput(
                    "current heading", curPose.getRotation().getRadians());

            org.littletonrobotics.junction.Logger.recordOutput(
                    "input rotational velocity", targetSpeeds.omegaRadiansPerSecond);
            org.littletonrobotics.junction.Logger.recordOutput(
                    "targetState", targetState.getTargetHolonomicPose());
            drive.setGoal(new Drive.RobotOrientedVelocity(targetSpeeds));
            context.yield();
        }

        if (path.getGoalEndState().getVelocity() < 0.1) {
            drive.setGoal(new Drive.SetCross());
        }
    }

    private void replanPath(Pose2d currentPose, ChassisSpeeds currentSpeeds) {
        PathPlannerPath replanned = path.replan(currentPose, currentSpeeds);
        generatedTrajectory =
                new PathPlannerTrajectory(replanned, currentSpeeds, currentPose.getRotation());
    }
}
