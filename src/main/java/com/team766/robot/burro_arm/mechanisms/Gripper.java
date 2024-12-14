package com.team766.robot.burro_arm.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.framework.Request;
import com.team766.framework.Status;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class Gripper extends Mechanism<Gripper.GripperRequest, Gripper.GripperStatus> {
    public record GripperStatus() implements Status {}

    public sealed interface GripperRequest extends Request {}

    public record Idle() implements GripperRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record Intake() implements GripperRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record Outtake() implements GripperRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    private final MotorController leftMotor;
    private final MotorController rightMotor;
    private final double intakePower =
            -ConfigFileReader.instance.getDouble("gripper.intakePower").valueOr(0.3);
    private final double outtakePower =
            ConfigFileReader.instance.getDouble("gripper.outtakePower").valueOr(0.1);
    private final double idlePower =
            -ConfigFileReader.instance.getDouble("gripper.idlePower").valueOr(0.05);

    public Gripper() {
        leftMotor = RobotProvider.instance.getMotor("gripper.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("gripper.rightMotor");
    }

    @Override
    protected GripperRequest getInitialRequest() {
        return new Idle();
    }

    @Override
    protected GripperRequest getIdleRequest() {
        return new Idle();
    }

    @Override
    protected GripperStatus run(GripperRequest request, boolean isRequestNew) {
        switch (request) {
            case Idle g -> {
                setMotorPower(idlePower);
            }
            case Intake g -> {
                setMotorPower(intakePower);
            }
            case Outtake g -> {
                setMotorPower(outtakePower);
            }
        }
        return new GripperStatus();
    }

    private void setMotorPower(final double power) {
        leftMotor.set(power);
        rightMotor.set(power);
    }
}
