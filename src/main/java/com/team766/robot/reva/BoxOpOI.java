package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.framework.LaunchedContext;
import com.team766.hal.JoystickReader;
import com.team766.logging.Category;
import com.team766.logging.Logger;
import com.team766.logging.Severity;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;
import com.team766.robot.reva.procedures.ShootNow;

public class BoxOpOI {
    private final JoystickReader gamepad;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;
    private final Climber climber;
    private LaunchedContext launchedContext;
    private boolean isAsyncFinished = true;

    public BoxOpOI(
            JoystickReader gamepad,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter,
            Climber climber) {
        Logger.get(Category.OPERATOR_INTERFACE).logRaw(Severity.INFO, "Creating BoxOpOI");
        this.gamepad = gamepad;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
        this.climber = climber;
    }

    public void handleOI(Context context) {

        // shoulder positions
        if (gamepad.getButtonPressed(InputConstants.XBOX_A)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
            context.releaseOwnership(shoulder);
        } else if (gamepad.getButtonPressed(InputConstants.XBOX_B)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.BOTTOM);
            context.releaseOwnership(shoulder);
        } else if (gamepad.getButtonPressed(InputConstants.XBOX_X)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.TOP);
            context.releaseOwnership(shoulder);
        } else if (gamepad.getButtonPressed(InputConstants.XBOX_Y)) {
            context.takeOwnership(shoulder);
            shoulder.rotate(ShoulderPosition.SHOOT_LOW);
            context.releaseOwnership(shoulder);
        }

        // dpad
        // shoulder up 5
        if (gamepad.getPOV() == 0) {
            context.takeOwnership(shoulder);
            shoulder.nudgeUp();
            context.releaseOwnership(shoulder);
        }

        if (gamepad.getPOV() == 180) {
            context.takeOwnership(shoulder);
            shoulder.nudgeDown();
            context.releaseOwnership(shoulder);
        }

        if (gamepad.getPOV() == 90) {
            context.takeOwnership(climber);
            // climber down method
            context.releaseOwnership(climber);
        }

        if (gamepad.getPOV() == 360) {
            context.takeOwnership(climber);
            // climber up method
            context.releaseOwnership(climber);
        }

        // shooter
        if (gamepad.getAxis(InputConstants.XBOX_RT) > 0) {
            // context.takeOwnership(shooter);
            // shooter.shoot();
            // context.releaseOwnership(shooter);
        } else if (isAsyncFinished) {
            context.takeOwnership(shooter);
            shooter.stop();
            context.releaseOwnership(shooter);
        }

        if (gamepad.getButtonPressed(8)) {
            isAsyncFinished = false;
            launchedContext = context.startAsync(new ShootNow());
        } else if (gamepad.getButtonReleased(8)
                || (launchedContext != null && launchedContext.isDone())) {
            Logger.get(Category.OPERATOR_INTERFACE).logRaw(Severity.INFO, "reset");
            if (launchedContext != null) {
                launchedContext.stop();
            }
            isAsyncFinished = true;
            launchedContext = null;
        }

        if (gamepad.getButtonPressed(7)) {
            context.takeOwnership(shooter);
            Robot.shooter.nudgeDown();
            context.releaseOwnership(shooter);
        }

        // intake
        if (gamepad.getButton(InputConstants.XBOX_RB)) {
            context.takeOwnership(intake);
            intake.out();
            context.releaseOwnership(intake);
        } else if (gamepad.getButton(InputConstants.XBOX_LB)) {
            context.takeOwnership(intake);
            intake.in();
            context.releaseOwnership(intake);
        } else if (isAsyncFinished) {
            context.takeOwnership(intake);
            intake.stop();
            context.releaseOwnership(intake);
        }
    }
}
