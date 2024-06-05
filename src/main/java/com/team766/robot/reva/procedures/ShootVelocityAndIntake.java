package com.team766.robot.reva.procedures;

import com.team766.framework.MagicProcedure;
import com.team766.framework.annotations.CollectReservations;
import com.team766.framework.annotations.Reserve;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.procedures.ShootingProcedureStatus.Status;

@CollectReservations
public final class ShootVelocityAndIntake
        extends MagicProcedure<ShootVelocityAndIntake_Reservations> {

    private final double speed;

    @Reserve Shooter shooter;

    @Reserve Intake intake;

    public ShootVelocityAndIntake() {
        this(4800);
    }

    public ShootVelocityAndIntake(double speed) {
        this.speed = speed;
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
