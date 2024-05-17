package com.team766.robot.reva.procedures.auton_routines;

import com.team766.framework.Context;
import com.team766.framework.ProcedureInterface;
import com.team766.robot.common.mechanisms.Drive;
import com.team766.robot.common.procedures.PathSequenceAuto;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.procedures.MoveClimbersToBottom;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

public class AutoBase extends PathSequenceAuto {

    private final Shooter shooter;

    public AutoBase(Drive drive, Shooter shooter, Climber climber, Pose2d initialPosition) {
        super(drive, initialPosition);
        this.shooter = shooter;
        addReservations(shooter);
        // TODO: Replace this with proper parallel execution
        addProcedure(new ProcedureInterface() {
            @Override
            public Set<Subsystem> getReservations() {
                return Set.of();
            }

            @Override
            public void execute(Context context) {
                context.startAsync(new MoveClimbersToBottom(climber));
            }
        });
    }

    @Override
    public void runAtEnd() {
        super.runAtEnd();
        shooter.setGoal(new Shooter.Stop());
    }
}
