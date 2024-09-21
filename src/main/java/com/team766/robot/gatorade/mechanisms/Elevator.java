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
 * Basic elevator mechanism.  Used in conjunction with the {@link Intake} and {@link Wrist}.
 * Can be moved up and down as part of teleop or autonomous control to move the {@link Wrist}
 * and {@link Intake} closer to a game piece or game element (eg node in the
 * field, human player station).
 */
public class Elevator extends Mechanism<Elevator.ElevatorRequest, Elevator.ElevatorStatus> {
    /**
     * @param height the current height of the elevator, in inches ('Murica).
     */
    public record ElevatorStatus(double rotations, double height) implements Status {
        public boolean isNearTo(MoveToPosition position) {
            return isNearTo(position.height());
        }

        public boolean isNearTo(double position) {
            return Math.abs(position - height) < NEAR_THRESHOLD;
        }
    }

    public sealed interface ElevatorRequest extends Request {}

    public record Stop() implements ElevatorRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public record NudgeNoPID(double value) implements ElevatorRequest {
        @Override
        public boolean isDone() {
            return true;
        }
    }

    public static ElevatorRequest makeHoldPosition() {
        final double currentHeight = getStatusOrThrow(ElevatorStatus.class).height();
        return new MoveToPosition(currentHeight);
    }

    public static ElevatorRequest makeNudgeUp() {
        final double currentHeight = getStatusOrThrow(ElevatorStatus.class).height();
        // NOTE: this could artificially limit nudge range
        final double targetHeight =
                Math.min(currentHeight + NUDGE_INCREMENT, MoveToPosition.EXTENDED.height());
        return new MoveToPosition(targetHeight);
    }

    public static ElevatorRequest makeNudgeDown() {
        final double currentHeight = getStatusOrThrow(ElevatorStatus.class).height();
        // NOTE: this could artificially limit nudge range
        final double targetHeight =
                Math.max(currentHeight - NUDGE_INCREMENT, MoveToPosition.RETRACTED.height());
        return new MoveToPosition(targetHeight);
    }

    /**
     * Moves the elevator to a specific position (in inches).
     */
    public record MoveToPosition(double height) implements ElevatorRequest {
        /** Elevator is fully retracted.  Starting position. */
        public static final MoveToPosition RETRACTED = new MoveToPosition(0);

        /** Elevator is the appropriate height to place game pieces at the low node. */
        public static final MoveToPosition LOW = new MoveToPosition(0);

        /** Elevator is the appropriate height to place game pieces at the mid node. */
        public static final MoveToPosition MID = new MoveToPosition(18);

        /** Elevator is at appropriate height to place game pieces at the high node. */
        public static final MoveToPosition HIGH = new MoveToPosition(40);

        /** Elevator is at appropriate height to grab cubes from the human player. */
        public static final MoveToPosition HUMAN_CUBES = new MoveToPosition(39);

        /** Elevator is at appropriate height to grab cones from the human player. */
        public static final MoveToPosition HUMAN_CONES = new MoveToPosition(40);

        /** Elevator is fully extended. */
        public static final MoveToPosition EXTENDED = new MoveToPosition(40);

        @Override
        public boolean isDone() {
            return checkForStatusWith(ElevatorStatus.class, s -> s.isNearTo(this));
        }
    }

    private static final double NUDGE_INCREMENT = 2.0;
    private static final double NUDGE_DAMPENER = 0.25;

    private static final double NEAR_THRESHOLD = 2.0;

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
     * Contructs a new Elevator.
     */
    public Elevator() {
        MotorController halLeftMotor = RobotProvider.instance.getMotor(ELEVATOR_LEFT_MOTOR);
        MotorController halRightMotor = RobotProvider.instance.getMotor(ELEVATOR_RIGHT_MOTOR);

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
                        EncoderUtils.elevatorHeightToRotations(MoveToPosition.RETRACTED.height()));

        pidController = leftMotor.getPIDController();
        pidController.setFeedbackDevice(leftMotor.getEncoder());

        pGain = ConfigFileReader.getInstance().getDouble(ELEVATOR_PGAIN);
        iGain = ConfigFileReader.getInstance().getDouble(ELEVATOR_IGAIN);
        dGain = ConfigFileReader.getInstance().getDouble(ELEVATOR_DGAIN);
        ffGain = ConfigFileReader.getInstance().getDouble(ELEVATOR_FFGAIN);
        maxVelocity = ConfigFileReader.getInstance().getDouble(ELEVATOR_MAX_VELOCITY);
        minOutputVelocity = ConfigFileReader.getInstance().getDouble(ELEVATOR_MIN_OUTPUT_VELOCITY);
        maxAccel = ConfigFileReader.getInstance().getDouble(ELEVATOR_MAX_ACCEL);
    }

    @Override
    protected ElevatorRequest getInitialRequest() {
        return new Stop();
    }

    @Override
    protected ElevatorStatus run(ElevatorRequest request, boolean isRequestNew) {
        final var status =
                new ElevatorStatus(
                        leftMotor.getEncoder().getPosition(),
                        EncoderUtils.elevatorRotationsToHeight(
                                leftMotor.getEncoder().getPosition()));

        switch (request) {
            case NudgeNoPID nudge -> {
                if (!isRequestNew) break;
                double clampedValue = MathUtil.clamp(nudge.value, -1, 1);
                clampedValue *=
                        NUDGE_DAMPENER; // make nudges less forceful.  TODO: make this non-linear
                leftMotor.set(clampedValue);
            }
            case Stop s -> {
                if (!isRequestNew) break;
                leftMotor.stopMotor();
            }
            case MoveToPosition position -> {
                applyPID(position.height);
            }
        }

        return status;
    }

    private void applyPID(double targetHeight) {
        // set the PID controller values with whatever the latest is in the config
        pidController.setP(pGain.get());
        pidController.setI(iGain.get());
        pidController.setD(dGain.get());
        // pidController.setFF(ffGain.get());
        double ff = ffGain.get();

        pidController.setOutputRange(-0.4, 0.4);

        // pidController.setSmartMotionAccelStrategy(AccelStrategy.kTrapezoidal, 0);
        // pidController.setSmartMotionMaxVelocity(maxVelocity.get(), 0);
        // pidController.setSmartMotionMinOutputVelocity(minOutputVelocity.get(), 0);
        // pidController.setSmartMotionMaxAccel(maxAccel.get(), 0);

        // convert the desired target degrees to encoder units
        double rotations = EncoderUtils.elevatorHeightToRotations(targetHeight);

        // SmartDashboard.putNumber("[ELEVATOR] ff", ff);
        SmartDashboard.putNumber("[ELEVATOR] reference", rotations);

        // set the reference point for the wrist
        pidController.setReference(rotations, ControlType.kPosition, 0, ff);
    }
}
