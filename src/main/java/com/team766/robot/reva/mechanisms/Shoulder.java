package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.SHOULDER_LEFT;
import static com.team766.robot.reva.constants.ConfigConstants.SHOULDER_RIGHT;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shoulder extends Mechanism {
    enum Position {
        // TODO: Find actual values.
        BOTTOM(0),
        INTAKE_FLOOR(0),
        SHOOT_LOW(35),
        SHOOT_MEDIUM(45),
        SHOOT_HIGH(80),
        TOP(90);

        private final double angle;

        Position(double angle) {
            this.angle = angle;
        }

        public double getAngle() {
            return angle;
        }
    }

    private static final double NUDGE_AMOUNT = 10; // degrees

    private MotorController leftMotor;
    private MotorController rightMotor;

    private double targetRotations = 0.0;

    public Shoulder() {
        // TODO: Initialize and use CANCoders to get offset for relative encoder on boot.
        leftMotor = RobotProvider.instance.getMotor(SHOULDER_LEFT);
        rightMotor = RobotProvider.instance.getMotor(SHOULDER_RIGHT);
        rightMotor.follow(leftMotor);
        leftMotor.setNeutralMode(NeutralMode.Brake);
    }

    public void nudgeUp() {
        rotate(getAngle() + NUDGE_AMOUNT);
    }

    public void nudgeDown() {
        rotate(getAngle() - NUDGE_AMOUNT);
    }

    public double getRotations() {
        return leftMotor.getSensorPosition();
    }

    public double getAngle() {
        return rotationsToDegrees(leftMotor.getSensorPosition());
    }

    private double degreesToRotations(double angle) {
        // angle * sprocket ratio * net gear ratio * (rotations / degrees)
        return angle * (54. / 15.) * (4. / 1.) * (3. / 1.) * (3. / 1.) * (1. / 360.);
    }

    private double rotationsToDegrees(double rotations) {
        // angle * sprocket ratio * net gear ratio * (degrees / rotations)
        return rotations * (15. / 54.) * (1. / 4.) * (1. / 3.) * (1. / 3.) * (360. / 1.);
    }

    public void rotate(Position position) {
        rotate(position.getAngle());
    }

    public void rotate(double angle) {
        checkContextOwnership();
        double targetAngle =
                Math.max(Position.BOTTOM.getAngle(), Math.min(Position.BOTTOM.getAngle(), angle));
        targetRotations = degreesToRotations(targetAngle);
        leftMotor.set(ControlMode.Position, targetRotations);
    }

    @Override
    public void run() {
        SmartDashboard.putNumber("[SHOULDER] Angle: ", getAngle());
        SmartDashboard.putNumber("[SHOULDER] Rotations: ", getRotations());
        SmartDashboard.putNumber("[SHOULDER] Target Rotations: ", targetRotations);
    }
}
