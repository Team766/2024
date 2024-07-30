package com.team766.robot.burro_arm.mechanisms;

import com.team766.config.ConfigFileReader;
import com.team766.framework.Mechanism;
import com.team766.hal.EncoderReader;
import com.team766.hal.MotorController;
import com.team766.hal.MotorController.ControlMode;
import com.team766.hal.RobotProvider;
import com.team766.library.RateLimiter;
import com.team766.library.ValueProvider;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm extends Mechanism {
    private static final double ABSOLUTE_ENCODER_TO_ARM_ANGLE =
            (360. /*degrees per rotation*/) * (12. / 54. /*chain reduction*/);
    private static final double MOTOR_ROTATIONS_TO_ARM_ANGLE =
            ABSOLUTE_ENCODER_TO_ARM_ANGLE * (1. / (5. * 5. * 5.) /*planetary gearbox*/);

    private final MotorController motor;
    private final EncoderReader absoluteEncoder;

    private final ValueProvider<Double> absoluteEncoderOffset;
    private final RateLimiter dashboardRateLimiter = new RateLimiter(0.1);

    private boolean initialized = false;

    public Arm() {
        motor = RobotProvider.instance.getMotor("arm.Motor");
        absoluteEncoder = RobotProvider.instance.getEncoder("arm.AbsoluteEncoder");
        absoluteEncoderOffset = ConfigFileReader.instance.getDouble("arm.AbsoluteEncoderOffset");
    }

    public void setPower(final double power) {
        checkContextOwnership();
        motor.set(power);
    }

    public void setAngle(final double angle) {
        checkContextOwnership();

        motor.set(ControlMode.Position, angle / MOTOR_ROTATIONS_TO_ARM_ANGLE);
    }

    public double getAngle() {
        return motor.getSensorPosition() * MOTOR_ROTATIONS_TO_ARM_ANGLE;
    }

    @Override
    public void run() {
        if (!initialized && absoluteEncoder.isConnected()) {
            final double absoluteEncoderPosition =
                    Math.IEEEremainder(
                            absoluteEncoder.getPosition() + absoluteEncoderOffset.get(), 1.0);
            SmartDashboard.putNumber(
                    "[ARM] AbsoluteEncoder Init Position", absoluteEncoderPosition);
            motor.setSensorPosition(
                    absoluteEncoderPosition
                            * ABSOLUTE_ENCODER_TO_ARM_ANGLE
                            / MOTOR_ROTATIONS_TO_ARM_ANGLE);
            initialized = true;
        }

        if (dashboardRateLimiter.next()) {
            SmartDashboard.putNumber("[Arm] Angle", getAngle());
        }
    }
}
