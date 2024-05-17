package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.procedures.ShootingProcedureStatus.Status;

public class ShootVelocityAndIntake extends Procedure {

    private final double speed;

    private final Shooter shooter;
    private final Intake intake;

    public ShootVelocityAndIntake(Shooter shooter, Intake intake) {
        this(4800, shooter, intake);
    }

    public ShootVelocityAndIntake(double speed, Shooter shooter, Intake intake) {
        super(reservations(shooter, intake));
        this.speed = speed;
        this.shooter = shooter;
        this.intake = intake;
    }

    public void run(Context context) {
        shooter.setGoal(new Shooter.ShootAtSpeed(speed));
        context.waitForConditionOrTimeout(() -> shooter.getStatus().isCloseToSpeed(speed), 1.5);

        intake.setGoal(new Intake.In());

        // FIXME: change this value back to 1.5s if doesn't intake for long enough
        context.waitForSeconds(1.2);

        intake.setGoal(new Intake.Stop());
        updateStatus(new ShootingProcedureStatus(Status.FINISHED));

        // Shooter stopped at the end of auton
    }
}
