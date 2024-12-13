package com.team766.robot.reva.procedures;

import com.team766.framework3.Context;
import com.team766.framework3.Procedure;
import com.team766.robot.reva.mechanisms.ArmAndClimber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;

public class ShootAtSubwoofer extends Procedure {
    private final ArmAndClimber superstructure;
    private final Shooter shooter;
    private final Intake intake;

    public ShootAtSubwoofer(ArmAndClimber superstructure, Shooter shooter, Intake intake) {
        this.superstructure = reserve(superstructure);
        this.shooter = reserve(shooter);
        this.intake = reserve(intake);
    }

    public void run(Context context) {
        superstructure.setRequest(Shoulder.RotateToPosition.SHOOT_LOW);
        context.runSync(new ShootVelocityAndIntake(shooter, intake));
    }
}
