package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class FinishIntakeAndShoot extends Procedure {
    private static final double DEFAULT_SHOOTER_SPEED = 20.0;
    private final double speed;

    public FinishIntakeAndShoot() {
        this(DEFAULT_SHOOTER_SPEED);
    }

    public FinishIntakeAndShoot(double speed) {
        this.speed = speed;
    }

    @Override
    public void run(Context context) {
        context.takeOwnership(Robot.intake);
        context.takeOwnership(Robot.shooter);

        Robot.shooter.shootSpeed(speed);
        context.waitFor(Robot.shooter::isCloseToExpectedSpeed);
        Robot.intake.in();
    }
}
