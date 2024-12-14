package com.team766.robot.common.mechanisms;

import static com.team766.robot.common.constants.ConfigConstants.*;

import com.team766.framework.Mechanism;
import com.team766.framework.Request;
import com.team766.framework.Status;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.logging.Category;

public class BurroDrive extends Mechanism<BurroDrive.DriveRequest, BurroDrive.DriveStatus> {
    public record DriveStatus() implements Status {}

    public sealed interface DriveRequest extends Request {}

    /*
     * Stops each drive motor
     */
    public record Stop() implements DriveRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    /**
     * @param forward how much power to apply to moving the robot (positive being forward)
     * @param turn how much power to apply to turning the robot (positive being CCW)
     */
    public record ArcadeDrive(double forward, double turn) implements DriveRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    private final MotorController leftMotor;
    private final MotorController rightMotor;

    public BurroDrive() {
        leftMotor = RobotProvider.instance.getMotor(DRIVE_LEFT);
        rightMotor = RobotProvider.instance.getMotor(DRIVE_RIGHT);
    }

    @Override
    public Category getLoggerCategory() {
        return Category.DRIVE;
    }

    @Override
    protected DriveRequest getInitialRequest() {
        return new Stop();
    }

    @Override
    protected DriveRequest getIdleRequest() {
        return new Stop();
    }

    @Override
    protected DriveStatus run(DriveRequest request, boolean isRequestNew) {
        switch (request) {
            case Stop g -> {
                leftMotor.stopMotor();
                rightMotor.stopMotor();
            }
            case ArcadeDrive g -> {
                leftMotor.set(g.forward - g.turn);
                rightMotor.set(g.forward + g.turn);
            }
        }
        return new DriveStatus();
    }
}
