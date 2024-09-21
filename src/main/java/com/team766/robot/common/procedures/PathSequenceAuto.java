package com.team766.robot.common.procedures;

import com.pathplanner.lib.util.GeometryUtil;
import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.common.mechanisms.SwerveDrive;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public abstract class PathSequenceAuto extends Procedure {

    private final SwerveDrive drive;
    private final Pose2d initialPosition;

    /**
     * Sequencer for using path following with other procedures
     * @param drive The instantiation of drive for the robot (pass in Robot.drive)
     * @param initialPosition Starting position on Blue Alliance in meters (gets flipped when on red)
     */
    public PathSequenceAuto(SwerveDrive drive, Pose2d initialPosition) {
        this.drive = reserve(drive);
        this.initialPosition = initialPosition;
    }

    protected void runPath(Context context, String pathName) {
        context.runSync(new FollowPath(pathName, drive));
    }

    protected abstract void runSequence(Context context);

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

        // if (!visionSpeakerHelper.updateTarget(context)) {
        drive.setCurrentPosition(
                shouldFlipAuton ? GeometryUtil.flipFieldPose(initialPosition) : initialPosition);
        // }
        drive.resetGyro(
                (shouldFlipAuton
                                ? GeometryUtil.flipFieldRotation(initialPosition.getRotation())
                                : initialPosition.getRotation())
                        .getDegrees());
        try {
            runSequence(context);
        } finally {
            // TODO: For some reason, the gyro is consistenty 180 degrees from expected in teleop
            // TODO: We should figure out why after EBR but for now we can just reset the gyro to
            // 180 of
            // current angle
            drive.resetGyro(180 + drive.getMechanismStatus().heading());
        }
    }
}
