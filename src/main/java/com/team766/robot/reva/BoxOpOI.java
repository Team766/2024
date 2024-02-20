package com.team766.robot.reva;

import com.team766.framework.Context;
import com.team766.hal.JoystickReader;
import com.team766.robot.reva.constants.InputConstants;
import com.team766.robot.reva.mechanisms.Climber;
import com.team766.robot.reva.mechanisms.Intake;
import com.team766.robot.reva.mechanisms.Shooter;
import com.team766.robot.reva.mechanisms.Shoulder;
import com.team766.robot.reva.mechanisms.Shoulder.ShoulderPosition;
import com.team766.robot.reva.mechanisms.Climber.ClimberPosition;

public class BoxOpOI {
	private final JoystickReader gamepad;

    private final Shoulder shoulder;
    private final Intake intake;
    private final Shooter shooter;
	private final Climber climber;

    public BoxOpOI(
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
        if(gamepad.getButton(InputConstants.XBOX_LB)){
			// shoulder presets
			context.takeOwnership(shoulder);

			if(gamepad.getButtonPressed(InputConstants.XBOX_A)){
				shoulder.rotate(ShoulderPosition.SHOOT_MEDIUM);
			} else if(gamepad.getButtonPressed(InputConstants.XBOX_B)){
				shoulder.rotate(ShoulderPosition.BOTTOM);
			} else if(gamepad.getButtonPressed(InputConstants.XBOX_X)){
				shoulder.rotate(ShoulderPosition.TOP);
			} else if(gamepad.getButtonPressed(InputConstants.XBOX_Y)){
				shoulder.rotate(ShoulderPosition.SHOOT_LOW);
			}

			context.releaseOwnership(shoulder);
		}

		if(gamepad.getAxis(InputConstants.XBOX_LT) > 0) {
			if(gamepad.getButton(InputConstants.XBOX_RB)){
				context.takeOwnership(intake);
				intake.out();
				context.releaseOwnership(intake);	
			} else {
				context.takeOwnership(intake);
				intake.in();
				context.releaseOwnership(intake);
			}

		} else {
			context.takeOwnership(intake);
			intake.stop();
			context.releaseOwnership(intake);
		}

		if(gamepad.getAxis(InputConstants.XBOX_RT) > 0) {
			context.takeOwnership(shooter);
			shooter.shoot();
			context.releaseOwnership(shooter);
		} else {
			context.takeOwnership(shooter);
			shooter.stop();
			context.releaseOwnership(shooter);
		}

		if (gamepad.getPOV() == InputConstants.POV_UP) {
			context.takeOwnership(climber);
			climber.setHeight(ClimberPosition.TOP);
			context.releaseOwnership(climber);
		} else if (gamepad.getPOV() == InputConstants.POV_DOWN) {
			context.takeOwnership(climber);
			climber.setHeight(ClimberPosition.BOTTOM);
			context.releaseOwnership(climber);
		}

		// if (gamepad.getButton(9)) {
		// 	context.takeOwnership(climber);
		// 	climber.();
		// 	context.releaseOwnership(climber);
		// }
		// if (gamepad.getButton(10)) {
		// 	context.takeOwnership(climber);
		// 	climber.nudgeDown();
		// 	context.releaseOwnership(climber);
		// }
		if (gamepad.getButton(7)) {
			context.takeOwnership(climber);
			climber.goNoPID();
			context.releaseOwnership(climber);
		} else {
			context.takeOwnership(climber);
			climber.stop();
			context.releaseOwnership(climber);
		}

		if (gamepad.getButtonPressed(8)) {
			context.takeOwnership(shoulder);
			shoulder.nudgeDown();
			context.releaseOwnership(shoulder);
		}
    }
}
