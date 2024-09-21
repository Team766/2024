package com.team766.robot.gatorade.mechanisms;

import static com.team766.framework3.Conditions.checkForStatusWith;
import static com.team766.framework3.StatusBus.getStatusOrThrow;
import static com.team766.robot.gatorade.constants.ConfigConstants.*;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkBase.ControlType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkPIDController;
import com.team766.config.ConfigFileReader;
import com.team766.framework3.Mechanism;
import com.team766.framework3.Request;
import com.team766.framework3.Status;
import com.team766.hal.MotorController;
import com.team766.hal.RobotProvider;
import com.team766.library.ValueProvider;
import com.team766.logging.Severity;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Basic shoulder mechanism.  Rotates the {@link Elevator} to different angles, to allow it (and the
 * attached {@link Wrist} and {@link Intake}) to reach different positions, from the floor to different
 * heights of nodes.
 */
public class Shoulder extends Mechanism<Shoulder.ShoulderRequest, Shoulder.ShoulderStatus> {

    /**
     * @param angle the current angle of the wrist.
     */
    public record ShoulderStatus(double rotations, double angle) implements Status {
        public boolean isNearTo(RotateToPosition position) {
            return isNearTo(position.angle());
        }

        public boolean isNearTo(double angle) {
            return Math.abs(angle - this.angle()) < NEAR_THRESHOLD;
        }
    }

    public sealed interface ShoulderRequest extends Request {}

    public record NudgeNoPID(double value) implements ShoulderRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record Stop() implements ShoulderRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public static ShoulderRequest makeHoldPosition() {
        final double currentAngle = getStatusOrThrow(ShoulderStatus.class).angle();
        return new RotateToPosition(currentAngle);
    }

    public static ShoulderRequest makeNudgeUp() {
        final double currentAngle = getStatusOrThrow(ShoulderStatus.class).angle();
        final double targetAngle =
                Math.min(currentAngle + NUDGE_INCREMENT, RotateToPosition.TOP.angle());
        return new RotateToPosition(targetAngle);
    }

    public static ShoulderRequest makeNudgeDown() {
        final double currentAngle = getStatusOrThrow(ShoulderStatus.class).angle();
        final double targetAngle =
                Math.max(currentAngle - NUDGE_INCREMENT, RotateToPosition.BOTTOM.angle());
        return new RotateToPosition(targetAngle);
    }

    /**
     * Starts rotating the wrist to the specified angle.
     */
    public record RotateToPosition(double angle) implements ShoulderRequest {
        // TODO: adjust these!

        /** Shoulder is at the highest achievable position. */
        public static final RotateToPosition TOP = new RotateToPosition(45);

        /** Shoulder is in position to intake from the substation or score in the upper nodes. */
        public static final RotateToPosition RAISED = new RotateToPosition(40);

        /** Shoulder is in position to intake and outtake pieces from/to the floor. */
        public static final RotateToPosition FLOOR = new RotateToPosition(10);

        /** Shoulder is fully down.  Starting position. **/
        public static final RotateToPosition BOTTOM = new RotateToPosition(0);

        @Override
        public boolean isDone() {
            return checkForStatusWith(ShoulderStatus.class, s -> s.isNearTo(this));
        }
    }

    private static final double NUDGE_INCREMENT = 5.0;
    private static final double NUDGE_DAMPENER = 0.15;

    private static final double NEAR_THRESHOLD = 5.0;

    private final CANSparkMax leftMotor;
    private final CANSparkMax rightMotor;
    private final SparkPIDController pidController;
    private final ValueProvider<Double> pGain;
    private final ValueProvider<Double> iGain;
    private final ValueProvider<Double> dGain;
    private final ValueProvider<Double> ffGain;
    private final ValueProvider<Double> maxVelocity;
    private final ValueProvider<Double> minOutputVelocity;
    private final ValueProvider<Double> maxAccel;

    /**
     * Constructs a new Shoulder.
     */
    public Shoulder() {
        MotorController halLeftMotor = RobotProvider.instance.getMotor(SHOULDER_LEFT_MOTOR);
        MotorController halRightMotor = RobotProvider.instance.getMotor(SHOULDER_RIGHT_MOTOR);

        if (!((halLeftMotor instanceof CANSparkMax) && (halRightMotor instanceof CANSparkMax))) {
            log(Severity.ERROR, "Motors are not CANSparkMaxes!");
            throw new IllegalStateException("Motor are not CANSparkMaxes!");
        }

        halLeftMotor.setNeutralMode(NeutralMode.Brake);
        halRightMotor.setNeutralMode(NeutralMode.Brake);

        leftMotor = (CANSparkMax) halLeftMotor;
        rightMotor = (CANSparkMax) halRightMotor;

        rightMotor.follow(leftMotor, true /* invert */);

        leftMotor
                .getEncoder()
                .setPosition(
                        EncoderUtils.shoulderDegreesToRotations(RotateToPosition.BOTTOM.angle()));

        pidController = leftMotor.getPIDController();
        pidController.setFeedbackDevice(leftMotor.getEncoder());

        pGain = ConfigFileReader.getInstance().getDouble(SHOULDER_PGAIN);
        iGain = ConfigFileReader.getInstance().getDouble(SHOULDER_IGAIN);
        dGain = ConfigFileReader.getInstance().getDouble(SHOULDER_DGAIN);
        ffGain = ConfigFileReader.getInstance().getDouble(SHOULDER_FFGAIN);
        maxVelocity = ConfigFileReader.getInstance().getDouble(SHOULDER_MAX_VELOCITY);
        minOutputVelocity = ConfigFileReader.getInstance().getDouble(SHOULDER_MIN_OUTPUT_VELOCITY);
        maxAccel = ConfigFileReader.getInstance().getDouble(SHOULDER_MAX_ACCEL);
    }

    @Override
    protected ShoulderRequest getInitialRequest() {
        return new Stop();
    }

    @Override
    protected ShoulderStatus run(ShoulderRequest request, boolean isRequestNew) {
        switch (request) {
            case NudgeNoPID nudge -> {
                if (!isRequestNew) break;
                double clampedValue = MathUtil.clamp(nudge.value, -1, 1);
                clampedValue *=
                        NUDGE_DAMPENER; // make nudges less forceful. TODO: make this non-linear
                leftMotor.set(clampedValue);
            }
            case Stop s -> {
                if (!isRequestNew) break;
                leftMotor.set(0);
            }
            case RotateToPosition position -> {
                applyPID(position.angle);
            }
        }

        return new ShoulderStatus(
                leftMotor.getEncoder().getPosition(),
                EncoderUtils.shoulderRotationsToDegrees(leftMotor.getEncoder().getPosition()));
    }

    private void applyPID(double targetAngle) {
        // set the PID controller values with whatever the latest is in the config
        pidController.setP(pGain.get());
        pidController.setI(iGain.get());
        pidController.setD(dGain.get());
        // pidController.setFF(ffGain.get());
        double ff = ffGain.get() * Math.cos(Math.toRadians(targetAngle));
        SmartDashboard.putNumber("[SHOULDER] ff", ff);
        SmartDashboard.putNumber("[SHOULDER] reference", targetAngle);

        pidController.setOutputRange(-0.4, 0.4);

        // convert the desired target degrees to rotations
        double rotations = EncoderUtils.shoulderDegreesToRotations(targetAngle);
        SmartDashboard.putNumber("[SHOULDER] Setpoint", rotations);

        // set the reference point for the wrist
        pidController.setReference(rotations, ControlType.kPosition, 0, ff);
    }
}
