package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.Context;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.procedures.MoveClimbersToBottom;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Collection;

public abstract class AutoBase extends PathSequenceAuto {

    private final Shooter shooter;
    private final Climber climber;

    public AutoBase(
            Collection<Subsystem> reservations,
            Drive drive,
            Shooter shooter,
            Climber climber,
            Pose2d initialPosition) {
        super(reservations, drive, initialPosition);
        addReservations(shooter);
        this.shooter = shooter;
        this.climber = climber;
    }

    protected abstract void runAuto(Context context);

    @Override
    protected final void runSequence(Context context) {
        // TODO: Replace this with proper parallel execution
        context.startAsync(new MoveClimbersToBottom(climber));
        runAuto(context);
    }

    @Override
    public void runAtEnd() {
        super.runAtEnd();
        shooter.setGoal(new Shooter.Stop());
    }
}
