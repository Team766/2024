package com.team766.robot.reva.procedures;

import static com.team766.framework3.Conditions.waitForRequestOrTimeout;
import static com.team766.framework3.StatusBus.publishStatus;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
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
        this.speed = speed;
        this.shooter = reserve(shooter);
        this.intake = reserve(intake);
    }

    public void run(Context context) {
        var speedRequest = new Shooter.ShootAtSpeed(speed);
        shooter.setRequest(speedRequest);
        waitForRequestOrTimeout(context, speedRequest, 1.5);

        intake.setRequest(new Intake.In());

        // FIXME: change this value back to 1.5s if doesn't intake for long enough
        context.waitForSeconds(1.0);

        intake.setRequest(new Intake.Stop());
        publishStatus(new ShootingProcedureStatus(Status.FINISHED));

        // Shooter stopped at the end of auton
    }
}
