package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;

public class BoxOpOI2 {
    private final JoystickReader gamepad;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;
    private final Climber climber;

    public BoxOpOI2(
            JoystickReader gamepad,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter,
            Climber climber) {
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
            context.takeOwnership(shooter);
            shooter.shoot();
            context.releaseOwnership(shooter);
        } else {
            context.takeOwnership(shooter);
            shooter.stop();
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
        } else {
            context.takeOwnership(intake);
            intake.stop();
            context.releaseOwnership(intake);
        }
    }
}
