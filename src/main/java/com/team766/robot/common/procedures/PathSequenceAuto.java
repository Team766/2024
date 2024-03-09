package com.team766.robot.common.procedures;

import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.util.PIDConstants;
import com.team766.config.ConfigFileReader;
import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.framework.RunnableWithContext;
import com.team766.robot.common.constants.ConfigConstants;
import com.team766.robot.common.constants.PathPlannerConstants;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.LinkedList;

public class PathSequenceAuto extends Procedure {

    private final LinkedList<RunnableWithContext> pathItems;
    private final Drive drive;
    private final Pose2d initialPosition;
    private final PPHolonomicDriveController controller;

    public PathSequenceAuto(Drive drive, Pose2d initialPosition) {
        pathItems = new LinkedList<RunnableWithContext>();
        this.drive = drive;
        this.controller = createDriveController(drive);
        this.initialPosition = initialPosition;
    }

    private PPHolonomicDriveController createDriveController(Drive drive) {
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

    protected void add(String pathName) {
        pathItems.add(new FollowPath(pathName, controller, drive, DriverStation.getAlliance().get() == Alliance.Red));
    }

    protected void add(Procedure procedure) {
        pathItems.add(procedure);
    }

    protected void add(double waitForSeconds) {
        pathItems.add((context) -> context.waitForSeconds(waitForSeconds));
    }

    @Override
    public final void run(Context context) {
        context.takeOwnership(Robot.drive);
        Robot.drive.setCurrentPosition(initialPosition);
        Robot.drive.resetGyro(initialPosition.getRotation().getDegrees() + (DriverStation.getAlliance().get() == Alliance.Red ? 180 : 0));

        for (RunnableWithContext pathItem : pathItems) {
            pathItem.run(context);
            context.yield();
        }
    }
}
