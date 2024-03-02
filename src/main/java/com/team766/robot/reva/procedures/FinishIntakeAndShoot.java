package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class FinishIntakeAndShoot extends Procedure {
    private static final double DEFAULT_SHOOTER_POWER = 0.95;
    private final double power;

    public FinishIntakeAndShoot() {
        this(DEFAULT_SHOOTER_POWER);
    }

    public FinishIntakeAndShoot(double power) {
        this.power = power;
    }

    @Override
    public void run(Context context) {
        context.takeOwnership(Robot.intake);
        context.takeOwnership(Robot.shooter);

        Robot.shooter.shootPower(power);
        context.waitFor(Robot.shooter::isCloseToExpectedSpeed);
        Robot.intake.in();
    }
}
