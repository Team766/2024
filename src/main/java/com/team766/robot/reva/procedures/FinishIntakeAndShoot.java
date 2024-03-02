package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class FinishIntakeAndShoot extends Procedure {
    private final double power;

    public FinishIntakeAndShoot(double power) {
        this.power = power;
    }

    @Override
    public void run(Context context) {
        context.takeOwnership(Robot.intake);
        context.takeOwnership(Robot.shooter);

        Robot.shooter.shootPower(power);
        // HACK.  replace with actually measuring velocity, once we switch to velocity-based PID
        context.waitForSeconds(3.0);
        Robot.intake.in();
    }
}
