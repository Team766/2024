package com.team766.robot.common.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.framework.RunnableWithContext;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.gatorade.Robot;
import edu.wpi.first.math.geometry.Pose2d;
import java.util.LinkedList;

public class PathSequenceAuto extends Procedure {

    private final LinkedList<RunnableWithContext> pathItems;
    private final Drive drive;
    private final Pose2d initialPosition;

    public PathSequenceAuto(Drive drive, Pose2d initialPosition) {
        pathItems = new LinkedList<RunnableWithContext>();
        this.drive = drive;
        this.initialPosition = initialPosition;
    }

    public void add(String pathName) {
        pathItems.add(new FollowPath(pathName, drive));
    }

    public void add(Procedure procedure) {
        pathItems.add(procedure);
    }

	public void add(double waitForSeconds) {
		pathItems.add((context) -> context.waitForSeconds(waitForSeconds));
	}

    @Override
    public final void run(Context context) {
        context.takeOwnership(Robot.drive);
        Robot.drive.resetGyro();
        Robot.drive.setCurrentPosition(initialPosition);

        for (RunnableWithContext pathItem : pathItems) {
            pathItem.run(context);
        }
    }
}
