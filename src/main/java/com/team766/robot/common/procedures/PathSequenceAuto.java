package com.team766.robot.common.procedures;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.controllers.PPLTVController;
import com.pathplanner.lib.controllers.PathFollowingController;
import com.pathplanner.lib.util.GeometryUtil;
import com.pathplanner.lib.util.PIDConstants;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.framework.RunnableWithContext;
import com.team766.robot.common.constants.ConfigConstants;
import com.team766.robot.common.constants.PathPlannerConstants;
import com.team766.robot.common.mechanisms.BurroDrive;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.common.mechanisms.SwerveDrive;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.procedures.MoveClimbersToBottom;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.LinkedList;
import java.util.Optional;

public class PathSequenceAuto extends Procedure {

    private final LinkedList<RunnableWithContext> pathItems;
    private final Drive drive;
    private final Pose2d initialPosition;
    private final PathFollowingController controller;

    /**
     * Sequencer for using path following with other procedures
     * @param drive The instantiation of drive for the robot (pass in Robot.drive)
     * @param initialPosition Starting position on Blue Alliance in meters (gets flipped when on red)
     */
    public PathSequenceAuto(Drive drive, Pose2d initialPosition) {
        pathItems = new LinkedList<RunnableWithContext>();
        this.drive = drive;
        if (drive instanceof SwerveDrive) {
            this.controller = createHolonomicDriveController((SwerveDrive) drive);
        } else {
            this.controller = new PPLTVController(0.02);
        }
        this.initialPosition = initialPosition;
    }

    private PPHolonomicDriveController createHolonomicDriveController(SwerveDrive drive) {
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

        return new PPHolonomicDriveController(
                new PIDConstants(translationP, translationI, translationD),
                new PIDConstants(rotationP, rotationI, rotationD),
                maxSpeed,
                drive.maxWheelDistToCenter());
    }

    protected void addPath(String pathName) {
        pathItems.add(new FollowPath(pathName, controller, drive));
    }

    protected void addProcedure(Procedure procedure) {
        pathItems.add(procedure);
    }

    protected void addWait(double waitForSeconds) {
        pathItems.add((context) -> context.waitForSeconds(waitForSeconds));
    }

    @Override
    public final void run(Context context) {
        boolean shouldFlipAuton = false;
        Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isPresent()) {
            shouldFlipAuton = (alliance.get() == Alliance.Red);
        } else {
            log("Unable to get Alliance for auton " + this.getClass().getSimpleName());
            log("Cannot determine if we should flip auton.");
            log("Skipping auton");
            return;
        }

        context.startAsync(new MoveClimbersToBottom());
        context.takeOwnership(drive);
        drive.setCurrentPosition(
                shouldFlipAuton ? GeometryUtil.flipFieldPose(initialPosition) : initialPosition);

        // context.takeOwnership(drive);
        drive.resetHeading(
                (shouldFlipAuton
                                ? GeometryUtil.flipFieldRotation(initialPosition.getRotation())
                                : initialPosition.getRotation())
                        .getDegrees());
        for (RunnableWithContext pathItem : pathItems) {
            context.runSync(pathItem);
            context.yield();
        }

        context.takeOwnership(Robot.shooter);
        Robot.shooter.stop();
        context.releaseOwnership(Robot.shooter);

        // TODO: For some reason, the gyro is consistenty 180 degrees from expected in teleop
        // TODO: We should figure out why after EBR but for now we can just reset the gyro to 180 of
        // current angle
        context.takeOwnership(drive);
        drive.resetHeading(180 + drive.getHeading());
        context.releaseOwnership(drive);
    }
}
