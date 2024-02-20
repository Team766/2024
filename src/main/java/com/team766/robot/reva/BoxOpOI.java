package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.Position;

public class BoxOpOI {
	private final JoystickReader gamepad;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;

    public BoxOpOI(
            JoystickReader gamepad,
            Shoulder shoulder,
            Intake intake,
            Shooter shooter) {
        this.gamepad = gamepad;
        this.shoulder = shoulder;
        this.intake = intake;
        this.shooter = shooter;
    }

    public void handleOI(Context context) {
        if(gamepad.getButton(InputConstants.XBOX_LB)){
			// shoulder presets
			context.takeOwnership(shoulder);

			if(gamepad.getButtonPressed(InputConstants.XBOX_A)){
				shoulder.rotate(Position.SHOOT_MEDIUM);
			} else if(gamepad.getButtonPressed(InputConstants.XBOX_B)){
				shoulder.rotate(Position.BOTTOM);
			} else if(gamepad.getButtonPressed(InputConstants.XBOX_X)){
				shoulder.rotate(Position.TOP);
			} else if(gamepad.getButtonPressed(InputConstants.XBOX_Y)){
				shoulder.rotate(Position.SHOOT_LOW);
			}

			context.releaseOwnership(shoulder);
		}

		if(gamepad.getAxis(InputConstants.XBOX_LT) > 0) {
			if(gamepad.getButton(InputConstants.XBOX_RT)){
				context.takeOwnership(intake);
				intake.out();
				context.releaseOwnership(intake);	
			} else {
				context.takeOwnership(intake);
				intake.runIntake();
				context.releaseOwnership(intake);
			}

		} else {
			context.takeOwnership(intake);
			intake.stop();
			context.releaseOwnership(intake);
		}

		if(gamepad.getAxis(InputConstants.XBOX_RT) > 0) {
			context.takeOwnership(shooter);
			Robot.shooter.runShooter();
			shooter.shoot();
			context.releaseOwnership(shooter);
		} else {
			context.takeOwnership(shooter);
			shooter.stop();
			context.releaseOwnership(shooter);
		}
    }
}
