package com.team766.robot.example.mechanisms;

import com.team766.framework3.Mechanism;
import com.team766.framework3.Request;
import com.team766.framework3.Status;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;

public class ExampleMechanism
        extends Mechanism<
                ExampleMechanism.ExampleMechanismRequest, ExampleMechanism.ExampleMechanismStatus> {
    public record ExampleMechanismStatus() implements Status {}

    public sealed interface ExampleMechanismRequest extends Request {}

    public record SetMotorPower(double leftPower, double rightPower)
            implements ExampleMechanismRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    private MotorController leftMotor;
    private MotorController rightMotor;

    public ExampleMechanism() {
        leftMotor = RobotProvider.instance.getMotor("exampleMechanism.leftMotor");
        rightMotor = RobotProvider.instance.getMotor("exampleMechanism.rightMotor");
    }

    @Override
    protected ExampleMechanismRequest getInitialRequest() {
        return new SetMotorPower(0, 0);
    }

    @Override
    protected ExampleMechanismStatus run(ExampleMechanismRequest request, boolean isRequestNew) {
        switch (request) {
            case SetMotorPower g -> {
                if (!isRequestNew) break;
                leftMotor.set(g.leftPower);
                rightMotor.set(g.rightPower);
            }
        }
        return new ExampleMechanismStatus();
    }
}
