package com.team766.robot.reva.mechanisms;

import com.team766.framework.Mechanism;
import static com.team766.robot.reva.constants.ConfigConstants.*;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.hal.MotorController.ControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shoulder extends Mechanism {
	
	private MotorController leftMotor;
	private MotorController rightMotor;

	enum Position {
		// TODO: Find actual values.
		Intake(0),
		ShootLow(35),
		ShootMedium(45),
		ShootHigh(80);

		private final double angle;

		Position(double angle) {
			this.angle = angle;
		}

		public double getAngle(){
			return angle;
		}
	}

	public Shoulder(){
		leftMotor = RobotProvider.instance.getMotor(SHOULDER_LEFT);
		rightMotor = RobotProvider.instance.getMotor(SHOULDER_RIGHT);

		rightMotor.follow(leftMotor);
	}

	public void nudgeUp(){
		rotate(getAngle() + SHOULDER_NUDGE_AMOUNT);
	}

	public void nudgeDown(){
		rotate(getAngle() - SHOULDER_NUDGE_AMOUNT);
	}
	
	public double getRotations() {
        return leftMotor.getSensorPosition();
    }
	public double getAngle() {
        return rotationsToDegrees(leftMotor.getSensorPosition());
    }

	private double degreesToRotations(double angle){
	// angle * sprocket ratio * net gear ratio * (rotations / degrees) 
	return angle * (54/13) * (4/1) * (3/1) * (3/1) * (360/1);
	}

	private double degreesToRotations(Position position){
		return degreesToRotations(position.getAngle());
	}

	private double rotationsToDegrees(double rotations){
		// angle * sprocket ratio * net gear ratio * (degrees / rotations)
		return rotations * (13/54) * (1/4) * (1/3) * (1/3) * (1/360);
	}

	public void rotate(Position position){
		rotate(position.getAngle());
		
	}
	public void rotate(double angle)
	{ 
		double rotations = degreesToRotations(angle);
		leftMotor.set(ControlMode.Position, rotations);
	}

	@Override
	public void run(){
		SmartDashboard.putNumber("Angle: ", getAngle());
		SmartDashboard.putNumber("Rotations: ", getRotations());
	}

}
