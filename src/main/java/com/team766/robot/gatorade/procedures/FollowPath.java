package com.team766.robot.gatorade.procedures;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.ReplanningConfig;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.odometry.Odometry;
import com.team766.odometry.PointDir;
import com.team766.robot.gatorade.Robot;
import com.team766.simulator.ui.Trajectory;

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

    public FollowPath(PathPlannerPath path, ReplanningConfig replanningConfig, PPHolonomicDriveController controller
     /* TODO: add flip path support */) {
        this.path = path;
        this.replanningConfig = replanningConfig;
        this.controller = controller;
    }

    @Override
    public void run(Context context) {
        context.takeOwnership(Robot.drive);

        // intitialization

        // TODO: flip path as necessary
        Pose2d curPose = getPose();
        // TODO: get actual speed
        ChassisSpeeds currentSpeeds = new ChassisSpeeds();

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
            curPose = getPose();
            // TODO: get actual speed
            currentSpeeds = new ChassisSpeeds();

            if (replanningConfig.enableDynamicReplanning) {
                // TODO: why abs?
                double previousError = Math.abs(controller.getPositionalError());
                double currentError = curPose.getTranslation().getDistance(targetState.positionMeters);

                if (currentError >= replanningConfig.dynamicReplanningTotalErrorThreshold
                    || currentError - previousError
                    // TODO: is this always negative?
                        >= replanningConfig.dynamicReplanningErrorSpikeThreshold) {
                    replanPath(curPose, currentSpeeds);
                    timer.reset();
                    targetState = generatedTrajectory.sample(0);
                }
            }

            ChassisSpeeds targetSpeeds = controller.calculateRobotRelativeSpeeds(curPose, targetState);
            
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

    private Pose2d getPose() {
        PointDir curPos_pd = Robot.drive.getCurrentPosition();
        return new Pose2d(curPos_pd.getX(), curPos_pd.getY(),
        new Rotation2d(curPos_pd.getHeading()));
    }
}
