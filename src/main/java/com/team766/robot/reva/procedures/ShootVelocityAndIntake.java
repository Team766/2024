package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.Shooter;

public class ShootVelocityAndIntake extends Procedure {

    private final double speed;

    private final Shooter shooter;
    private final Intake intake;
    private final Lights lights;

    public ShootVelocityAndIntake(Shooter shooter, Intake intake, Lights lights) {
        this(4800, shooter, intake, lights);
    }

    public ShootVelocityAndIntake(double speed, Shooter shooter, Intake intake, Lights lights) {
        super(reservations(shooter, intake));
        this.speed = speed;
        this.shooter = shooter;
        this.intake = intake;
        this.lights = lights;
    }

    public void run(Context context) {
        shooter.shoot(speed);
        context.waitForConditionOrTimeout(shooter::isCloseToExpectedSpeed, 1.5);

        context.runSync(new IntakeIn(intake));

        // FIXME: change this value back to 1.5s if doesn't intake for long enough
        context.waitForSeconds(1.2);

        context.runSync(new IntakeStop(intake));
        lights.signalFinishedShootingProcedure();

        // Shooter stopped at the end of auton
    }
}
