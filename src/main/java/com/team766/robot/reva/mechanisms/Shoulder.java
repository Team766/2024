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

    private static final double SHOULDER_NUDGE_AMOUNT = 10; // degrees

    private MotorController leftMotor;
    private MotorController rightMotor;

    public Shoulder() {
        // TODO: Initialize and use CANCoders to get offset for relative encoder on boot.
        leftMotor = RobotProvider.instance.getMotor(SHOULDER_LEFT);
        rightMotor = RobotProvider.instance.getMotor(SHOULDER_RIGHT);
        rightMotor.follow(leftMotor);
        leftMotor.setNeutralMode(NeutralMode.Brake);
    }

    public void nudgeUp() {
        double angle = getAngle();
        double targetAngle = Math.min(angle + SHOULDER_NUDGE_AMOUNT, Position.TOP.getAngle());
        rotate(targetAngle);
    }

    public void nudgeDown() {
        double angle = getAngle();
        double targetAngle = Math.max(angle - SHOULDER_NUDGE_AMOUNT, Position.BOTTOM.getAngle());
        rotate(targetAngle);
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
        double rotations = degreesToRotations(angle);
        leftMotor.set(ControlMode.Position, rotations);
    }

    @Override
    public void run() {
        SmartDashboard.putNumber("[SHOULDER] Angle: ", getAngle());
        SmartDashboard.putNumber("[SHOULDER] Rotations: ", getRotations());
    }
}
