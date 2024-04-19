package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class DriverShootVelocityAndIntake extends Procedure {

    public void run(Context context) {

        context.waitForConditionOrTimeout(Robot.shooter::isCloseToExpectedSpeed, 1);

        context.runSync(new IntakeIn());

        // Does not stop intake here so driver can stop when button released
    }
}
