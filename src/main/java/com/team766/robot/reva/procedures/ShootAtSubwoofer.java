package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Lights;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;

public class ShootAtSubwoofer extends Procedure {
    private final Shoulder shoulder;
    private final Shooter shooter;
    private final Intake intake;
    private final Lights lights;

    public ShootAtSubwoofer(Shoulder shoulder, Shooter shooter, Intake intake, Lights lights) {
        super(reservations(shoulder, shooter, intake));
        this.shoulder = shoulder;
        this.shooter = shooter;
        this.intake = intake;
        this.lights = lights;
    }

    public void run(Context context) {
        shoulder.rotate(ShoulderPosition.SHOOT_LOW);
        context.runSync(new ShootVelocityAndIntake(shooter, intake, lights));
    }
}
