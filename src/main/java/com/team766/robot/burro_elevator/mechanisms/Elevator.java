package com.team766.robot.burro_elevator.mechanisms;

import com.team766.framework.Mechanism;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import com.team766.hal.wpilib.CANSparkMaxMotorController;
import com.team766.library.RateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends Mechanism {
    private static final double MOTOR_ROTATIONS_TO_ELEVATOR_POSITION =
            (0.25 /*chain pitch = distance per tooth*/)
                    * (18. /*teeth per rotation of sprocket*/)
                    * (1. / (3. * 4. * 4.) /*planetary gearbox*/);

    private final CANSparkMaxMotorController motor;
    private final RateLimiter dashboardRateLimiter = new RateLimiter(0.1);

    public Elevator() {
        motor = (CANSparkMaxMotorController)RobotProvider.instance.getMotor("elevator.Motor");
        motor.setSmartCurrentLimit(10, 80, 200);
    }

    public void setPower(final double power) {
        checkContextOwnership();
        motor.set(power);
    }

    public void setPosition(final double position) {
        checkContextOwnership();

        motor.set(ControlMode.Position, position / MOTOR_ROTATIONS_TO_ELEVATOR_POSITION);
    }

    public double getPosition() {
        return motor.getSensorPosition() * MOTOR_ROTATIONS_TO_ELEVATOR_POSITION;
    }

    @Override
    public void run() {
        if (dashboardRateLimiter.next()) {
            SmartDashboard.putNumber("[Elevator] Position", getPosition());
        }
    }
}
