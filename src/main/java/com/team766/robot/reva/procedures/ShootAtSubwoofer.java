package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Superstructure;

public class ShootAtSubwoofer extends Procedure {
    private final Superstructure superstructure;
    private final Shooter shooter;
    private final Intake intake;

    public ShootAtSubwoofer(Superstructure superstructure, Shooter shooter, Intake intake) {
        super(reservations(superstructure, shooter, intake));
        this.superstructure = superstructure;
        this.shooter = shooter;
        this.intake = intake;
    }

    public void run(Context context) {
        superstructure.setGoal(Shoulder.RotateToPosition.SHOOT_LOW);
        context.runSync(new ShootVelocityAndIntake(shooter, intake));
    }
}
