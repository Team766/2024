package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;

public class ShootAtSubwoofer extends Procedure {
    public void run(Context context) {
        context.takeOwnership(Robot.shoulder);
        Robot.shoulder.rotate(ShoulderPosition.SHOOT_LOW);
        context.releaseOwnership(Robot.shoulder);
        context.runSync(new ShootVelocityAndIntake());
    }
}
