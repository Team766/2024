package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class ShootVelocityAndIntake extends Procedure {

    double speed;

    public ShootVelocityAndIntake() {
        this(4800);
    }

    public ShootVelocityAndIntake(double speed) {
        this.speed = speed;
    }

    public void run(Context context) {
        context.takeOwnership(Robot.shooter);

        Robot.shooter.shoot(speed);
        context.waitForConditionOrTimeout(Robot.shooter::isCloseToExpectedSpeed, 1.5);

        new IntakeIn().run(context);
        context.waitForSeconds(1.1);

        new IntakeStop().run(context);
        Robot.lights.signalFinishedShootingProcedure();

        // Shooter stopped at the end of auton
    }
}
