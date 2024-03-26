package com.team766.robot.reva.procedures;

import com.team766.framework.Context;
import com.team766.framework.Procedure;
import com.team766.robot.reva.Robot;

public class CheckDevices extends Procedure {
    @Override
    public void run(Context context) {
        boolean check = true;
        context.takeOwnership(Robot.drive);
        check &= Robot.drive.checkDevices();
        context.releaseOwnership(Robot.drive);

        context.takeOwnership(Robot.climber);
        check &= Robot.climber.checkDevices();
        context.releaseOwnership(Robot.climber);

        context.takeOwnership(Robot.forwardApriltagCamera);
        check &= Robot.forwardApriltagCamera.checkDevices();
        context.releaseOwnership(Robot.forwardApriltagCamera);

        context.takeOwnership(Robot.intake);
        check &= Robot.intake.checkDevices();
        context.releaseOwnership(Robot.intake);

        context.takeOwnership(Robot.noteCamera);
        check &= Robot.noteCamera.checkDevices();
        context.releaseOwnership(Robot.noteCamera);

        context.takeOwnership(Robot.shooter);
        check &= Robot.shooter.checkDevices();
        context.releaseOwnership(Robot.shooter);

        context.takeOwnership(Robot.shoulder);
        check &= Robot.shoulder.checkDevices();
        context.releaseOwnership(Robot.shoulder);

        if (check) {
            Robot.lights.signalDeviceCheckHealthy();
        } else {
            Robot.lights.signalDeviceCheckUnhealthy();
        }
    }
}
