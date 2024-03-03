package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class AimAndShoot extends Procedure {
    // TODO: add rotation
    private final double angle;
    private final double speed;

    private static final double DEFAULT_SHOOTER_SPEED = 20.0;
    private static final double DEFAULT_SHOULDER_ANGLE = 10.0;

    public AimAndShoot() {
        this(DEFAULT_SHOULDER_ANGLE, DEFAULT_SHOOTER_SPEED);
    }

    public AimAndShoot(double angle, double speed) {
        this.angle = angle;
        this.speed = speed;
    }

    @Override
    public void run(Context context) {
        context.takeOwnership(Robot.shoulder);
        context.takeOwnership(Robot.intake);
        context.takeOwnership(Robot.shooter);

        Robot.shoulder.rotate(angle);
        Robot.shooter.shootSpeed(speed);

        context.waitFor(Robot.shoulder::isCloseToTargetAngle);
        context.waitFor(Robot.shooter::isCloseToExpectedSpeed);
        Robot.intake.in();
    }
}
