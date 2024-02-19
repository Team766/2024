package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends Mechanism {

    public enum Position {
        TOP(43.18),
        BOTTOM(0);

        private final double height;

        private Position(double height) {
            this.height = height;
        }

        public double getHeight() {
            return height;
        }
    }

    private MotorController leftMotor;
    private MotorController rightMotor;

    private double targetRotations = 0;

    private static final double GEAR_RATIO_AND_CIRCUMFERENCE =
            (14. / 50.) * (30. / 42.) * (1.25 * Math.PI);
    private static final double NUDGE_AMOUNT = 10; // in cm

    public Climber() {
        leftMotor = RobotProvider.instance.getMotor(CLIMBER_LEFT_MOTOR);
        rightMotor = RobotProvider.instance.getMotor(CLIMBER_RIGHT_MOTOR);
        // rightMotor.follow(leftMotor);

        leftMotor.setNeutralMode(NeutralMode.Brake);
    }

    private double heightToRotations(double height) {
        return height * GEAR_RATIO_AND_CIRCUMFERENCE;
    }

    private double rotationsToHeight(double rotations) {
        return rotations / GEAR_RATIO_AND_CIRCUMFERENCE;
    }

    public void setHeight(Position position) {
        setHeight(position.getHeight());
    }

    public void setHeight(double height) {
        double targetHeight =
                Math.max(Position.BOTTOM.getHeight(), Math.min(height, Position.TOP.getHeight()));
        targetRotations = heightToRotations(targetHeight);
        leftMotor.set(MotorController.ControlMode.Position, targetRotations);
    }

    public double getHeight() {
        return rotationsToHeight(leftMotor.getSensorPosition());
    }

    public void nudgeUp() {
        setHeight(getHeight() + NUDGE_AMOUNT);
    }

    public void nudgeDown() {
        setHeight(getHeight() - NUDGE_AMOUNT);
    }

    @Override
    public void run() {
        SmartDashboard.putNumber("[CLIMBER] Rotations", leftMotor.getSensorPosition());
        SmartDashboard.putNumber("[CLIMBER] Target Rotations", targetRotations);
        SmartDashboard.putNumber("[CLIMBER] Height", getHeight());
    }
}
