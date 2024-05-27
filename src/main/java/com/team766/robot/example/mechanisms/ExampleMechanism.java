package com.team766.robot.example.mechanisms;

import com.team766.framework.RobotSystem;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class ExampleMechanism extends RobotSystem<ExampleMechanism.Status, ExampleMechanism.Goal> {
    public record Status() {}

    public sealed interface Goal {}

    public record SetMotorPower(double leftPower, double rightPower) implements Goal {}

    private MotorController leftMotor;
    private MotorController rightMotor;

    public ExampleMechanism() {
        leftMotor = RobotProvider.instance.getMotor("exampleMechanism.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("exampleMechanism.rightMotor");
    }

    @Override
    protected Status updateState() {
        return new Status();
    }

    @Override
    protected void dispatch(Status status, Goal goal, boolean goalChanged) {
        switch (goal) {
            case SetMotorPower g -> {
                leftMotor.set(g.leftPower);
                rightMotor.set(g.rightPower);
            }
        }
    }
}
