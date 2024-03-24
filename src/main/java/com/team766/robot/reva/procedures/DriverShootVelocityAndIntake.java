package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class DriverShootVelocityAndIntake extends Procedure {

    double speed;

    public DriverShootVelocityAndIntake() {
        this(5600);
    }

    public DriverShootVelocityAndIntake(double speed) {
        this.speed = speed;
    }

    public void run(Context context) {
        // context.takeOwnership(Robot.shooter);

        // Robot.shooter.shoot(speed);
        context.waitForConditionOrTimeout(Robot.shooter::isCloseToExpectedSpeed, 1);
        // context.waitForSeconds(0.5);

        context.takeOwnership(Robot.intake);
        new IntakeIn().run(context);
        context.waitForSeconds(1.5);

        new IntakeStop().run(context);
        // Robot.shooter.stop();
    }
}
