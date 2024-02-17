package com.team766.robot.reva.mechanisms;

import static com.team766.robot.reva.constants.ConfigConstants.*;
import com.team766.framework.Mechanism;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends Mechanism {

    private MotorController leftMotor;
    private MotorController rightMotor;

    // TODO: find real value
    private static final double GEAR_RATIO = 1;

    public Climber() {
        leftMotor = RobotProvider.instance.getMotor(CLIMBER_LEFT_MOTOR);
        rightMotor = RobotProvider.instance.getMotor(CLIMBER_RIGHT_MOTOR);
        rightMotor.follow(leftMotor);
    }

    private double heightToRotations(double height) {
        return height * GEAR_RATIO;
    }

    private double rotationsToHeight(double rotations) {
        return rotations / GEAR_RATIO;
    }

    public void setClimbPosition(double TargetHeight) {
        double r = heightToRotations(TargetHeight);
        leftMotor.set(MotorController.ControlMode.Position, r);
    }

    public double getClimberPosition() {
        return rotationsToHeight(leftMotor.getSensorPosition());
    }

    public void nudgeUp() {
        setClimbPosition(getClimberPosition() + 1);
        // one nudge is ##### cm
    }

    public void nudgeDown() {
        setClimbPosition(getClimberPosition() + 1);
        // one nudge is ##### cm
    }

    @Override
    public void run() {
        SmartDashboard.putNumber("[CLIMBER] Rotations", leftMotor.getSensorPosition());

        SmartDashboard.putNumber("[CLIMBER] Position", getClimberPosition());
    }
}
